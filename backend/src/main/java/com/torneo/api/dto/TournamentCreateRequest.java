package com.torneo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TournamentCreateRequest(
        @NotBlank(message = "El nombre del torneo es obligatorio")
        @Size(min = 3, max = 150)
        String name,

        String description,
        String category
) {}
