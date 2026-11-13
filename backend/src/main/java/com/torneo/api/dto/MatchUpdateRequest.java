package com.torneo.api.dto;

public record MatchUpdateRequest(
        Integer scoreA,
        Integer scoreB,
        String winnerId,
        String status,
        String notes
) {}
