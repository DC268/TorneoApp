package com.torneo.api.dto;

import java.time.Instant;

public record MatchResponse(
        String id,
        String tournamentId,
        Integer roundNumber,
        String roundName,
        Integer matchNumber,
        ParticipantResponse participantA,
        ParticipantResponse participantB,
        Integer scoreA,
        Integer scoreB,
        ParticipantResponse winner,
        String status,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
