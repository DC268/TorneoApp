package com.torneo.mtg.service;

import com.torneo.mtg.api.dto.MatchDtos.*;
import com.torneo.mtg.domain.*;
import com.torneo.mtg.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public MatchService(
            MatchRepository matchRepository,
            MatchPlayerRepository matchPlayerRepository,
            ParticipantRepository participantRepository,
            TournamentRepository tournamentRepository,
            UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.participantRepository = participantRepository;
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
    }

    public List<MatchResponse> getMatches(UUID tournamentId) {
        return matchRepository.findByTournamentIdOrderByRoundNumberAscTableNumberAsc(tournamentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public List<MatchResponse> generateRandomPairings(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        List<Participant> participants = new ArrayList<>(participantRepository.findByTournamentId(tournamentId));
        if (participants.size() < 2) {
            throw new IllegalStateException("Se requieren al menos 2 participantes para generar emparejamientos");
        }

        // Shuffle randomly
        Collections.shuffle(participants);

        // Clear existing matches for this round or tournament
        matchRepository.deleteByTournamentId(tournamentId);

        boolean isCommander = "COMMANDER".equalsIgnoreCase(tournament.getFormat());
        int groupSize = isCommander ? 4 : 2;
        int startingLife = isCommander ? 40 : 20;

        List<Match> generatedMatches = new ArrayList<>();
        int tableNumber = 1;

        for (int i = 0; i < participants.size(); i += groupSize) {
            int end = Math.min(i + groupSize, participants.size());
            List<Participant> group = participants.subList(i, end);

            Match match = new Match(tournament, tableNumber++, 1);
            match.setStatus("IN_PROGRESS");

            List<MatchPlayer> matchPlayers = new ArrayList<>();
            int seat = 1;
            for (Participant p : group) {
                MatchPlayer mp = new MatchPlayer(match, p.getUser(), seat++, startingLife);
                matchPlayers.add(mp);
            }
            match.setPlayers(matchPlayers);

            // Handle BYE if a single player is left in 1v1
            if (group.size() == 1) {
                match.setStatus("COMPLETED");
                match.setWinner(group.get(0).getUser());
                group.get(0).setMatchPoints(group.get(0).getMatchPoints() + 3);
                participantRepository.save(group.get(0));
            }

            match = matchRepository.save(match);
            generatedMatches.add(match);
        }

        tournament.setStatus("ACTIVE");
        tournament.setCurrentRound(1);
        tournamentRepository.save(tournament);

        return generatedMatches.stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public MatchResponse submitResult(UUID matchId, SubmitResultRequest req) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado"));

        User winner = null;
        if (req.winnerId() != null) {
            winner = userRepository.findById(req.winnerId()).orElse(null);
        }
        match.setWinner(winner);
        match.setStatus("COMPLETED");

        if (req.scores() != null) {
            for (PlayerScoreDto scoreDto : req.scores()) {
                match.getPlayers().stream()
                        .filter(mp -> mp.getUser().getId().equals(scoreDto.userId()))
                        .findFirst()
                        .ifPresent(mp -> mp.setScore(scoreDto.score()));
            }
        }

        // Update participant match points (3 pts for win, 1 pt for draw)
        if (winner != null) {
            participantRepository.findByTournamentIdAndUserId(match.getTournament().getId(), winner.getId())
                    .ifPresent(p -> {
                        p.setMatchPoints(p.getMatchPoints() + 3);
                        participantRepository.save(p);
                    });
        }

        match = matchRepository.save(match);
        return mapToResponse(match);
    }

    @Transactional
    public MatchResponse updateLife(UUID matchId, UpdateLifeRequest req) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado"));

        match.getPlayers().stream()
                .filter(mp -> mp.getUser().getId().equals(req.userId()))
                .findFirst()
                .ifPresent(mp -> {
                    mp.setCurrentLife(mp.getCurrentLife() + req.lifeChange());
                    mp.setPoisonCounters(Math.max(0, mp.getPoisonCounters() + req.poisonChange()));
                    if (mp.getCurrentLife() <= 0 || mp.getPoisonCounters() >= 10) {
                        mp.setEliminated(true);
                    }
                });

        match = matchRepository.save(match);
        return mapToResponse(match);
    }

    private MatchResponse mapToResponse(Match m) {
        List<MatchPlayerResponse> players = m.getPlayers().stream()
                .map(p -> new MatchPlayerResponse(
                        p.getId(),
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getSeatPosition(),
                        p.getCurrentLife(),
                        p.getPoisonCounters(),
                        p.isEliminated(),
                        p.getScore()
                )).toList();

        return new MatchResponse(
                m.getId(),
                m.getTournament().getId(),
                m.getTableNumber(),
                m.getRoundNumber(),
                m.getStatus(),
                m.getWinner() != null ? m.getWinner().getId() : null,
                m.getWinner() != null ? m.getWinner().getName() : null,
                players
        );
    }
}
