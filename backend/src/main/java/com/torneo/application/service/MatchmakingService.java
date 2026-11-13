package com.torneo.application.service;

import com.torneo.api.dto.MatchResponse;
import com.torneo.api.dto.MatchUpdateRequest;
import com.torneo.api.dto.MatchmakingRequest;
import com.torneo.api.exception.ApiException;
import com.torneo.domain.entity.MatchEntity;
import com.torneo.domain.entity.ParticipantEntity;
import com.torneo.domain.entity.TournamentEntity;
import com.torneo.domain.model.MatchStatus;
import com.torneo.domain.model.TournamentStatus;
import com.torneo.infrastructure.persistence.repository.MatchRepository;
import com.torneo.infrastructure.persistence.repository.ParticipantRepository;
import com.torneo.infrastructure.persistence.repository.TournamentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MatchmakingService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;

    public MatchmakingService(
            MatchRepository matchRepository,
            TournamentRepository tournamentRepository,
            ParticipantRepository participantRepository,
            ParticipantService participantService
    ) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.participantService = participantService;
    }

    @Transactional
    public List<MatchResponse> generateRandomMatchmaking(MatchmakingRequest request) {
        TournamentEntity tournament = tournamentRepository.findById(request.tournamentId())
                .orElseThrow(() -> new ApiException("TOURNAMENT_NOT_FOUND", "Torneo no encontrado", HttpStatus.NOT_FOUND));

        List<ParticipantEntity> participants = participantRepository.findByTournamentIdOrderByCreatedAtAsc(tournament.getId());
        if (participants.size() < 2) {
            throw new ApiException("NOT_ENOUGH_PARTICIPANTS", "Se necesitan al menos 2 personas inscritas para realizar el emparejamiento", HttpStatus.BAD_REQUEST);
        }

        // Clean previous matches if any
        matchRepository.deleteByTournamentId(tournament.getId());

        // Randomly shuffle participants
        List<ParticipantEntity> shuffled = new ArrayList<>(participants);
        Collections.shuffle(shuffled, new SecureRandom());

        int totalParticipants = shuffled.size();
        String roundName = getInitialRoundName(totalParticipants);

        List<MatchEntity> generatedMatches = new ArrayList<>();
        int matchNumber = 1;

        for (int i = 0; i < totalParticipants; i += 2) {
            ParticipantEntity playerA = shuffled.get(i);
            ParticipantEntity playerB = (i + 1 < totalParticipants) ? shuffled.get(i + 1) : null;

            MatchEntity match = new MatchEntity();
            match.setId(UUID.randomUUID().toString());
            match.setTournament(tournament);
            match.setRoundNumber(1);
            match.setRoundName(roundName);
            match.setMatchNumber(matchNumber++);
            match.setParticipantA(playerA);
            match.setParticipantB(playerB);
            match.setCreatedAt(Instant.now());

            if (playerB == null) {
                // BYE: Odd number, advances automatically
                match.setStatus(MatchStatus.COMPLETED);
                match.setWinner(playerA);
                match.setScoreA(1);
                match.setScoreB(0);
                match.setNotes("Pase directo por número impar (BYE)");
            } else {
                match.setStatus(MatchStatus.PENDING);
                match.setNotes("Emparejamiento aleatorio generado");
            }

            generatedMatches.add(match);
        }

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournamentRepository.save(tournament);

        List<MatchEntity> savedMatches = matchRepository.saveAll(generatedMatches);
        return savedMatches.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByTournament(String tournamentId) {
        return matchRepository.findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(tournamentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchResponse updateMatchResult(String matchId, MatchUpdateRequest request) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ApiException("MATCH_NOT_FOUND", "Encuentro no encontrado con ID: " + matchId, HttpStatus.NOT_FOUND));

        if (request.scoreA() != null) {
            match.setScoreA(request.scoreA());
        }
        if (request.scoreB() != null) {
            match.setScoreB(request.scoreB());
        }
        if (request.notes() != null) {
            match.setNotes(request.notes());
        }

        if (request.winnerId() != null && !request.winnerId().isBlank()) {
            ParticipantEntity winner = participantRepository.findById(request.winnerId())
                    .orElseThrow(() -> new ApiException("PARTICIPANT_NOT_FOUND", "Participante ganador no encontrado", HttpStatus.NOT_FOUND));

            // Verify winner is one of the participants
            boolean isPlayerA = match.getParticipantA() != null && match.getParticipantA().getId().equals(winner.getId());
            boolean isPlayerB = match.getParticipantB() != null && match.getParticipantB().getId().equals(winner.getId());
            if (!isPlayerA && !isPlayerB) {
                throw new ApiException("INVALID_WINNER", "El ganador debe ser uno de los dos participantes del encuentro", HttpStatus.BAD_REQUEST);
            }
            match.setWinner(winner);
        }

        if (request.status() != null && !request.status().isBlank()) {
            try {
                match.setStatus(MatchStatus.valueOf(request.status().toUpperCase()));
            } catch (IllegalArgumentException e) {
                match.setStatus(MatchStatus.COMPLETED);
            }
        } else if (match.getWinner() != null) {
            match.setStatus(MatchStatus.COMPLETED);
        }

        match.setUpdatedAt(Instant.now());
        MatchEntity updated = matchRepository.save(match);

        // Check if all matches in the current round are completed, and if so and there are multiple winners, prompt or advance
        checkAndAdvanceNextRoundIfPossible(match.getTournament().getId(), match.getRoundNumber());

        return toResponse(updated);
    }

    private void checkAndAdvanceNextRoundIfPossible(String tournamentId, int currentRound) {
        List<MatchEntity> roundMatches = matchRepository.findByTournamentIdAndRoundNumber(tournamentId, currentRound);
        boolean allCompleted = roundMatches.stream().allMatch(m -> m.getStatus() == MatchStatus.COMPLETED && m.getWinner() != null);

        if (allCompleted && roundMatches.size() > 1) {
            // Check if next round already exists
            List<MatchEntity> nextRoundMatches = matchRepository.findByTournamentIdAndRoundNumber(tournamentId, currentRound + 1);
            if (nextRoundMatches.isEmpty()) {
                List<ParticipantEntity> winners = roundMatches.stream().map(MatchEntity::getWinner).collect(Collectors.toList());
                int nextRound = currentRound + 1;
                String nextRoundName = getNextRoundName(winners.size());

                List<MatchEntity> newMatches = new ArrayList<>();
                int mNum = 1;
                for (int i = 0; i < winners.size(); i += 2) {
                    ParticipantEntity pA = winners.get(i);
                    ParticipantEntity pB = (i + 1 < winners.size()) ? winners.get(i + 1) : null;

                    MatchEntity nextMatch = new MatchEntity();
                    nextMatch.setId(UUID.randomUUID().toString());
                    nextMatch.setTournament(roundMatches.get(0).getTournament());
                    nextMatch.setRoundNumber(nextRound);
                    nextMatch.setRoundName(nextRoundName);
                    nextMatch.setMatchNumber(mNum++);
                    nextMatch.setParticipantA(pA);
                    nextMatch.setParticipantB(pB);
                    nextMatch.setCreatedAt(Instant.now());

                    if (pB == null) {
                        nextMatch.setStatus(MatchStatus.COMPLETED);
                        nextMatch.setWinner(pA);
                        nextMatch.setScoreA(1);
                        nextMatch.setScoreB(0);
                        nextMatch.setNotes("Pase automático por BYE");
                    } else {
                        nextMatch.setStatus(MatchStatus.PENDING);
                    }
                    newMatches.add(nextMatch);
                }
                matchRepository.saveAll(newMatches);
            }
        } else if (allCompleted && roundMatches.size() == 1) {
            // Final match completed! Set tournament status to FINISHED
            TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElse(null);
            if (tournament != null) {
                tournament.setStatus(TournamentStatus.FINISHED);
                tournamentRepository.save(tournament);
            }
        }
    }

    private String getInitialRoundName(int count) {
        if (count <= 2) return "Gran Final";
        if (count <= 4) return "Semifinales";
        if (count <= 8) return "Cuartos de Final";
        if (count <= 16) return "Octavos de Final";
        return "Ronda Preliminar 1";
    }

    private String getNextRoundName(int winnersCount) {
        if (winnersCount <= 2) return "Gran Final";
        if (winnersCount <= 4) return "Semifinales";
        if (winnersCount <= 8) return "Cuartos de Final";
        return "Ronda " + winnersCount;
    }

    public MatchResponse toResponse(MatchEntity entity) {
        if (entity == null) return null;
        return new MatchResponse(
                entity.getId(),
                entity.getTournament() != null ? entity.getTournament().getId() : null,
                entity.getRoundNumber(),
                entity.getRoundName(),
                entity.getMatchNumber(),
                participantService.toResponse(entity.getParticipantA()),
                participantService.toResponse(entity.getParticipantB()),
                entity.getScoreA(),
                entity.getScoreB(),
                participantService.toResponse(entity.getWinner()),
                entity.getStatus().name(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
