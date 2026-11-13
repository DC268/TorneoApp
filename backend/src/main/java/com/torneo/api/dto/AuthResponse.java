package com.torneo.api.dto;

public record AuthResponse(
        String token,
        UserDto user
) {
    public static AuthResponse of(String token, UserDto user) {
        return new AuthResponse(token, user);
    }
}
