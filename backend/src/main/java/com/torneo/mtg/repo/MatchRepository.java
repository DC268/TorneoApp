package com.torneo.mtg.repo;

import com.torneo.mtg.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findByTournamentIdOrderByRoundNumberAscTableNumberAsc(UUID tournamentId);
    List<Match> findByTournamentIdAndRoundNumber(UUID tournamentId, int roundNumber);
    void deleteByTournamentId(UUID tournamentId);
}
