package com.torneo.api.controller;

import com.torneo.api.dto.ParticipantCreateRequest;
import com.torneo.api.dto.ParticipantResponse;
import com.torneo.application.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * Pestaña 1 del Administrador: Inscribir personas a un torneo
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParticipantResponse> registerParticipant(@Valid @RequestBody ParticipantCreateRequest request) {
        ParticipantResponse response = participantService.registerParticipant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<ParticipantResponse>> getParticipantsByTournament(@PathVariable String tournamentId) {
        return ResponseEntity.ok(participantService.getParticipantsByTournament(tournamentId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteParticipant(@PathVariable String id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }
}
