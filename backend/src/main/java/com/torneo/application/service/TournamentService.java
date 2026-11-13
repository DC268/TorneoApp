package com.torneo.application.service;

import com.torneo.api.dto.TournamentCreateRequest;
import com.torneo.api.dto.TournamentDto;
import com.torneo.api.exception.ApiException;
import com.torneo.domain.entity.TournamentEntity;
import com.torneo.domain.model.TournamentStatus;
import com.torneo.infrastructure.persistence.repository.MatchRepository;
import com.torneo.infrastructure.persistence.repository.ParticipantRepository;
import com.torneo.infrastructure.persistence.repository.TournamentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final MatchRepository matchRepository;

    public TournamentService(
            TournamentRepository tournamentRepository,
            ParticipantRepository participantRepository,
            MatchRepository matchRepository
    ) {
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public TournamentDto createTournament(TournamentCreateRequest request) {
        TournamentEntity tournament = new TournamentEntity(
                UUID.randomUUID().toString(),
                request.name().trim(),
                request.description(),
                request.category() != null ? request.category().trim() : "General"
        );
        TournamentEntity saved = tournamentRepository.save(tournament);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TournamentDto getTournamentById(String id) {
        TournamentEntity tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ApiException("TOURNAMENT_NOT_FOUND", "Torneo no encontrado con ID: " + id, HttpStatus.NOT_FOUND));
        return toDto(tournament);
    }

    @Transactional
    public void deleteTournament(String id) {
        if (!tournamentRepository.existsById(id)) {
            throw new ApiException("TOURNAMENT_NOT_FOUND", "Torneo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
        }
        matchRepository.deleteByTournamentId(id);
        participantRepository.deleteByTournamentId(id);
        tournamentRepository.deleteById(id);
    }

    public TournamentDto toDto(TournamentEntity entity) {
        int pCount = (int) participantRepository.countByTournamentId(entity.getId());
        int mCount = matchRepository.findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(entity.getId()).size();
        return new TournamentDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getStatus().name(),
                pCount,
                mCount,
                entity.getCreatedAt()
        );
    }
}
