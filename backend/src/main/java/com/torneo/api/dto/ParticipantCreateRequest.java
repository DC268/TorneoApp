package com.torneo.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParticipantCreateRequest(
        @NotBlank(message = "El torneo es obligatorio")
        String tournamentId,

        @NotBlank(message = "El nombre de la persona es obligatorio")
        @Size(min = 2, max = 150)
        String name,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "Formato de correo inválido")
        String email,

        String phone,
        String identification,
        String category
) {}
