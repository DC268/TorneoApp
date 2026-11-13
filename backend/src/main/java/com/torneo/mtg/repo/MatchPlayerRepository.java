package com.torneo.mtg.repo;

import com.torneo.mtg.domain.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, UUID> {
    Optional<MatchPlayer> findByMatchIdAndUserId(UUID matchId, UUID userId);
}
