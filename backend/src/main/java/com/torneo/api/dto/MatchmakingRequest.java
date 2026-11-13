package com.torneo.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MatchmakingRequest(
        @NotBlank(message = "El identificador del torneo es obligatorio")
        String tournamentId,

        Boolean reshuffleExisting
) {}
