package com.torneo.api.dto;

import java.time.Instant;

public record TournamentDto(
        String id,
        String name,
        String description,
        String category,
        String status,
        int participantsCount,
        int matchesCount,
        Instant createdAt
) {}
