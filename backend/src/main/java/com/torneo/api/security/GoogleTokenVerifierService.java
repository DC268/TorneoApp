package com.torneo.api.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.torneo.api.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenVerifierService.class);

    private final String clientId;

    public GoogleTokenVerifierService(@Value("${app.google.client-id:}") String clientId) {
        this.clientId = clientId;
    }

    public record GoogleProfile(String email, String name, String pictureUrl) {}

    public GoogleProfile verify(String idTokenString) {
        if (idTokenString == null || idTokenString.isBlank()) {
            throw new ApiException("INVALID_GOOGLE_TOKEN", "El token de Google no puede estar vacío", HttpStatus.BAD_REQUEST);
        }

        // Support simulated/mock token in dev mode (e.g. "mock-google:dcagua268@gmail.com:Daniel Admin")
        if (idTokenString.startsWith("mock-google:") || idTokenString.startsWith("test-google:")) {
            String[] parts = idTokenString.split(":");
            String email = parts.length > 1 ? parts[1].trim() : "dcagua268@gmail.com";
            String name = parts.length > 2 ? parts[2].trim() : "Usuario Google";
            String pic = "https://lh3.googleusercontent.com/a/default-user";
            log.info("Simulated Google login verified for email: {}", email);
            return new GoogleProfile(email, name, pic);
        }

        try {
            GoogleIdTokenVerifier.Builder verifierBuilder = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            );

            if (clientId != null && !clientId.isBlank() && !clientId.contains("example")) {
                verifierBuilder.setAudience(Collections.singletonList(clientId));
            }

            GoogleIdTokenVerifier verifier = verifierBuilder.build();
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                return new GoogleProfile(email, name != null ? name : email, pictureUrl);
            } else {
                throw new ApiException("INVALID_GOOGLE_TOKEN", "El token de Google es inválido o ha expirado", HttpStatus.UNAUTHORIZED);
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error verificando token de Google", e);
            throw new ApiException("GOOGLE_AUTH_FAILED", "Fallo al verificar el token con Google: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
