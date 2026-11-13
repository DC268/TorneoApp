package com.torneo.mtg.service;

import com.torneo.mtg.api.dto.TournamentDtos.*;
import com.torneo.mtg.domain.Participant;
import com.torneo.mtg.domain.Tournament;
import com.torneo.mtg.domain.User;
import com.torneo.mtg.repo.ParticipantRepository;
import com.torneo.mtg.repo.TournamentRepository;
import com.torneo.mtg.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public TournamentService(
            TournamentRepository tournamentRepository,
            ParticipantRepository participantRepository,
            UserRepository userRepository) {
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    public List<TournamentResponse> getAllTournaments() {
        return tournamentRepository.findByOrderByCreatedAtDesc().stream()
                .map(t -> {
                    int count = participantRepository.findByTournamentId(t.getId()).size();
                    return new TournamentResponse(
                            t.getId(), t.getName(), t.getFormat(), t.getStatus(),
                            t.getTotalRounds(), t.getCurrentRound(), count, t.getCreatedAt()
                    );
                }).toList();
    }

    public Tournament getTournament(UUID id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));
    }

    @Transactional
    public TournamentResponse createTournament(CreateTournamentRequest req) {
        String format = (req.format() != null && !req.format().isBlank()) ? req.format().toUpperCase() : "MODERN";
        int rounds = req.totalRounds() > 0 ? req.totalRounds() : 3;
        Tournament tournament = new Tournament(req.name(), format, rounds);
        tournament = tournamentRepository.save(tournament);
        return new TournamentResponse(
                tournament.getId(), tournament.getName(), tournament.getFormat(),
                tournament.getStatus(), tournament.getTotalRounds(), tournament.getCurrentRound(),
                0, tournament.getCreatedAt()
        );
    }

    public List<ParticipantResponse> getParticipants(UUID tournamentId) {
        return participantRepository.findByTournamentId(tournamentId).stream()
                .map(p -> new ParticipantResponse(
                        p.getId(),
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getUser().getEmail(),
                        p.getDeckName(),
                        p.getMatchPoints(),
                        p.getRegisteredAt()
                )).toList();
    }

    @Transactional
    public ParticipantResponse registerParticipant(UUID tournamentId, RegisterParticipantRequest req) {
        Tournament tournament = getTournament(tournamentId);

        User user;
        if (req.userId() != null) {
            user = userRepository.findById(req.userId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        } else {
            String email = (req.email() != null && !req.email().isBlank())
                    ? req.email().toLowerCase().trim()
                    : "player-" + UUID.randomUUID().toString().substring(0, 8) + "@torneo.local";
            String name = (req.name() != null && !req.name().isBlank())
                    ? req.name().trim()
                    : "Jugador " + (participantRepository.findByTournamentId(tournamentId).size() + 1);

            user = userRepository.findByEmail(email).orElseGet(() ->
                    userRepository.save(new User(email, name, null, "PLAYER"))
            );
        }

        if (participantRepository.existsByTournamentIdAndUserId(tournamentId, user.getId())) {
            throw new IllegalArgumentException("El jugador ya esta inscrito en este torneo");
        }

        Participant participant = new Participant(tournament, user, req.deckName());
        participant = participantRepository.save(participant);

        return new ParticipantResponse(
                participant.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                participant.getDeckName(),
                participant.getMatchPoints(),
                participant.getRegisteredAt()
        );
    }

    @Transactional
    public void removeParticipant(UUID tournamentId, UUID userId) {
        participantRepository.deleteByTournamentIdAndUserId(tournamentId, userId);
    }
}
