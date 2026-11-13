package com.torneo.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
        String name,

        @NotBlank(message = "El correo electrónico es requerido")
        @Email(message = "Formato de correo electrónico inválido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{6,}$",
                message = "La contraseña debe contener al menos letras y números, y un mínimo de 6 caracteres"
        )
        String password,

        String role
) {}
