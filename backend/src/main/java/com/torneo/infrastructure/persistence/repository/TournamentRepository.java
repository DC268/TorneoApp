package com.torneo.infrastructure.persistence.repository;

import com.torneo.domain.entity.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, String> {
    List<TournamentEntity> findAllByOrderByCreatedAtDesc();
}
