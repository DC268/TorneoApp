package com.torneo.infrastructure.persistence.repository;

import com.torneo.domain.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, String> {
    List<MatchEntity> findByTournamentIdOrderByRoundNumberAscMatchNumberAsc(String tournamentId);
    List<MatchEntity> findByTournamentIdAndRoundNumber(String tournamentId, Integer roundNumber);
    void deleteByTournamentId(String tournamentId);
}
