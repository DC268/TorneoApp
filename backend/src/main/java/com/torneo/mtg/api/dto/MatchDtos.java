package com.torneo.mtg.api.dto;

import java.util.List;
import java.util.UUID;

public class MatchDtos {

    public record MatchPlayerResponse(
            UUID id,
            UUID userId,
            String name,
            int seatPosition,
            int currentLife,
            int poisonCounters,
            boolean isEliminated,
            int score
    ) {}

    public record MatchResponse(
            UUID id,
            UUID tournamentId,
            int tableNumber,
            int roundNumber,
            String status,
            UUID winnerId,
            String winnerName,
            List<MatchPlayerResponse> players
    ) {}

    public record SubmitResultRequest(
            UUID winnerId,
            List<PlayerScoreDto> scores
    ) {}

    public record PlayerScoreDto(
            UUID userId,
            int score
    ) {}

    public record UpdateLifeRequest(
            UUID userId,
            int lifeChange,
            int poisonChange
    ) {}
}
