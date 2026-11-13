package com.torneo.mtg.repo;

import com.torneo.mtg.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    List<Participant> findByTournamentId(UUID tournamentId);
    Optional<Participant> findByTournamentIdAndUserId(UUID tournamentId, UUID userId);
    boolean existsByTournamentIdAndUserId(UUID tournamentId, UUID userId);
    void deleteByTournamentIdAndUserId(UUID tournamentId, UUID userId);
}
