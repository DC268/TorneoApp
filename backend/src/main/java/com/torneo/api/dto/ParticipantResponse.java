package com.torneo.api.dto;

import java.time.Instant;

public record ParticipantResponse(
        String id,
        String tournamentId,
        String name,
        String email,
        String phone,
        String identification,
        String category,
        Integer seedNumber,
        String status,
        Instant createdAt
) {}
