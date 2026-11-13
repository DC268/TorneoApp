package com.torneo.mtg.service;

import com.torneo.mtg.api.dto.AuthDtos.*;
import com.torneo.mtg.domain.User;
import com.torneo.mtg.repo.UserRepository;
import com.torneo.mtg.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final List<String> adminEmails;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            @Value("${app.security.admin-emails:dcagua268@gmail.com}") List<String> adminEmails) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.adminEmails = adminEmails.stream().map(String::toLowerCase).map(String::trim).toList();
    }

    public String resolveRole(String email) {
        if (email == null) return "PLAYER";
        return adminEmails.contains(email.toLowerCase().trim()) ? "ADMIN" : "PLAYER";
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email().toLowerCase().trim())) {
            throw new IllegalArgumentException("El correo electronico ya esta registrado");
        }

        String role = resolveRole(req.email());
        User user = new User(
                req.email().toLowerCase().trim(),
                req.name().trim(),
                passwordEncoder.encode(req.password()),
                role
        );
        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getEloRating(), token);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email().toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getEloRating(), token);
    }

    @Transactional
    public AuthResponse googleAuth(GoogleAuthRequest req) {
        String email = req.email() != null ? req.email().toLowerCase().trim() : "google.user@example.com";
        String name = req.name() != null ? req.name().trim() : "Usuario Google";

        Optional<User> existing = userRepository.findByEmail(email);
        User user;
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            String role = resolveRole(email);
            user = new User(email, name, null, role);
            user = userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getEloRating(), token);
    }
}
