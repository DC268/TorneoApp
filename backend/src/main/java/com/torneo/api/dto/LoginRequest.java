package com.torneo.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El correo electrónico es requerido")
        @Email(message = "Formato de correo inválido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        String password
) {}
