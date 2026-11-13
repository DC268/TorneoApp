package com.torneo.mtg.repo;

import com.torneo.mtg.domain.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
    List<Tournament> findByOrderByCreatedAtDesc();
}
