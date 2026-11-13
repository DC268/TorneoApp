package com.torneo.application.service;

import com.torneo.api.dto.ParticipantCreateRequest;
import com.torneo.api.dto.ParticipantResponse;
import com.torneo.api.exception.ApiException;
import com.torneo.domain.entity.ParticipantEntity;
import com.torneo.domain.entity.TournamentEntity;
import com.torneo.infrastructure.persistence.repository.ParticipantRepository;
import com.torneo.infrastructure.persistence.repository.TournamentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;

    public ParticipantService(
            ParticipantRepository participantRepository,
            TournamentRepository tournamentRepository
    ) {
        this.participantRepository = participantRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Transactional
    public ParticipantResponse registerParticipant(ParticipantCreateRequest req) {
        TournamentEntity tournament = tournamentRepository.findById(req.tournamentId())
                .orElseThrow(() -> new ApiException("TOURNAMENT_NOT_FOUND", "Torneo no encontrado", HttpStatus.NOT_FOUND));

        String email = req.email().trim().toLowerCase();
        if (participantRepository.existsByTournamentIdAndEmail(tournament.getId(), email)) {
            throw new ApiException("PARTICIPANT_ALREADY_REGISTERED", "Ya existe una persona inscrita con este correo en este torneo", HttpStatus.CONFLICT);
        }

        long currentCount = participantRepository.countByTournamentId(tournament.getId());

        ParticipantEntity participant = new ParticipantEntity(
                UUID.randomUUID().toString(),
                req.name().trim(),
                email,
                req.phone() != null ? req.phone().trim() : null,
                req.identification() != null ? req.identification().trim() : null,
                req.category() != null ? req.category().trim() : tournament.getCategory(),
                tournament
        );
        participant.setSeedNumber((int) currentCount + 1);

        ParticipantEntity saved = participantRepository.save(participant);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ParticipantResponse> getParticipantsByTournament(String tournamentId) {
        return participantRepository.findByTournamentIdOrderByCreatedAtAsc(tournamentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteParticipant(String participantId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ApiException("PARTICIPANT_NOT_FOUND", "Participante no encontrado", HttpStatus.NOT_FOUND));
        participantRepository.delete(participant);
    }

    public ParticipantResponse toResponse(ParticipantEntity entity) {
        if (entity == null) return null;
        return new ParticipantResponse(
                entity.getId(),
                entity.getTournament() != null ? entity.getTournament().getId() : null,
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getIdentification(),
                entity.getCategory(),
                entity.getSeedNumber(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
