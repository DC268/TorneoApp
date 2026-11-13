package com.torneo.mtg.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public class TournamentDtos {

    public record CreateTournamentRequest(
            @NotBlank(message = "El nombre del torneo es obligatorio")
            String name,
            String format, // STANDARD, MODERN, COMMANDER
            int totalRounds
    ) {}

    public record RegisterParticipantRequest(
            UUID userId,
            String name,
            String email,
            String deckName
    ) {}

    public record ParticipantResponse(
            UUID id,
            UUID userId,
            String name,
            String email,
            String deckName,
            int matchPoints,
            Instant registeredAt
    ) {}

    public record TournamentResponse(
            UUID id,
            String name,
            String format,
            String status,
            int totalRounds,
            int currentRound,
            int participantsCount,
            Instant createdAt
    ) {}
}
