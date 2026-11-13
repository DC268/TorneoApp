package com.torneo.mtg.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank(message = "El nombre es obligatorio")
            String name,

            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no es valido")
            String email,

            @NotBlank(message = "La contrasena es obligatoria")
            @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{6,}$",
                message = "La contrasena debe tener al menos 6 caracteres y combinar letras y numeros"
            )
            String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no es valido")
            String email,

            @NotBlank(message = "La contrasena es obligatoria")
            String password
    ) {}

    public record GoogleAuthRequest(
            @NotBlank(message = "El token de Google es obligatorio")
            String idToken,
            String email,
            String name
    ) {}

    public record AuthResponse(
            UUID id,
            String name,
            String email,
            String role,
            int eloRating,
            String token
    ) {}
}
