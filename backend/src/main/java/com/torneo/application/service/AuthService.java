package com.torneo.application.service;

import com.torneo.api.dto.*;
import com.torneo.api.exception.ApiException;
import com.torneo.api.security.GoogleTokenVerifierService;
import com.torneo.api.security.JwtTokenProvider;
import com.torneo.domain.entity.UserEntity;
import com.torneo.domain.model.Role;
import com.torneo.infrastructure.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleTokenVerifierService googleVerifier;
    private final String adminEmail;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            GoogleTokenVerifierService googleVerifier,
            @Value("${app.admin.email:dcagua268@gmail.com}") String adminEmail
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.googleVerifier = googleVerifier;
        this.adminEmail = adminEmail.trim().toLowerCase();
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String normalizedEmail = req.email().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ApiException("EMAIL_ALREADY_EXISTS", "El correo electrónico ya se encuentra registrado", HttpStatus.CONFLICT);
        }

        // Validate password must have letters and numbers
        validatePassword(req.password());

        // Role determination: if email is admin email or requested ADMIN, handle appropriately
        Role role = Role.CLIENT;
        if (normalizedEmail.equalsIgnoreCase(adminEmail) || "ADMIN".equalsIgnoreCase(req.role())) {
            role = Role.ADMIN;
        }

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID().toString());
        user.setName(req.name().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(role);
        user.setAuthProvider("LOCAL");
        user.setCreatedAt(Instant.now());

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return AuthResponse.of(token, toDto(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String normalizedEmail = req.email().toLowerCase().trim();
        UserEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ApiException("INVALID_CREDENTIALS", "Credenciales incorrectas (correo o contraseña no válidos)", HttpStatus.UNAUTHORIZED));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ApiException("INVALID_CREDENTIALS", "Credenciales incorrectas (correo o contraseña no válidos)", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return AuthResponse.of(token, toDto(user));
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest req) {
        GoogleTokenVerifierService.GoogleProfile profile = googleVerifier.verify(req.idToken());
        String normalizedEmail = profile.email().toLowerCase().trim();

        UserEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setId(UUID.randomUUID().toString());
                    newUser.setName(profile.name() != null ? profile.name() : "Usuario Google");
                    newUser.setEmail(normalizedEmail);
                    newUser.setAvatarUrl(profile.pictureUrl());
                    newUser.setAuthProvider("GOOGLE");
                    newUser.setCreatedAt(Instant.now());

                    // If it is the admin email, assign ADMIN role automatically
                    if (normalizedEmail.equalsIgnoreCase(adminEmail)) {
                        newUser.setRole(Role.ADMIN);
                    } else {
                        newUser.setRole(Role.CLIENT);
                    }
                    return userRepository.save(newUser);
                });

        // Ensure admin email always maintains ADMIN role even if previously logged in differently
        if (normalizedEmail.equalsIgnoreCase(adminEmail) && user.getRole() != Role.ADMIN) {
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return AuthResponse.of(token, toDto(user));
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ApiException("USER_NOT_FOUND", "Usuario no encontrado", HttpStatus.NOT_FOUND));
        return toDto(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ApiException("INVALID_PASSWORD", "La contraseña debe tener al menos 6 caracteres", HttpStatus.BAD_REQUEST);
        }
        boolean hasLetters = password.chars().anyMatch(Character::isLetter);
        boolean hasDigits = password.chars().anyMatch(Character::isDigit);
        if (!hasLetters || !hasDigits) {
            throw new ApiException("INVALID_PASSWORD", "La contraseña debe contener obligatoriamente letras y números", HttpStatus.BAD_REQUEST);
        }
    }

    public UserDto toDto(UserEntity user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getAvatarUrl()
        );
    }
}
