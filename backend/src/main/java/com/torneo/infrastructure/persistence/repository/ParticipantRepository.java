package com.torneo.infrastructure.persistence.repository;

import com.torneo.domain.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, String> {
    List<ParticipantEntity> findByTournamentIdOrderByCreatedAtAsc(String tournamentId);
    boolean existsByTournamentIdAndEmail(String tournamentId, String email);
    long countByTournamentId(String tournamentId);
    void deleteByTournamentId(String tournamentId);
}
