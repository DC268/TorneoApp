package com.torneo.mtg.api.controller;

import com.torneo.mtg.api.dto.MatchDtos.MatchResponse;
import com.torneo.mtg.api.dto.TournamentDtos.*;
import com.torneo.mtg.service.MatchService;
import com.torneo.mtg.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final MatchService matchService;

    public TournamentController(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @PostMapping
    public ResponseEntity<?> createTournament(@Valid @RequestBody CreateTournamentRequest req) {
        try {
            TournamentResponse res = tournamentService.createTournament(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Pestaña 1: Inscribir personas a un torneo
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getParticipants(id));
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerParticipant(
            @PathVariable UUID id,
            @RequestBody RegisterParticipantRequest req) {
        try {
            ParticipantResponse res = tournamentService.registerParticipant(id, req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/participants/{userId}")
    public ResponseEntity<?> removeParticipant(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        tournamentService.removeParticipant(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Pestaña 2: Emparejar automaticamente y de forma random
    @PostMapping("/{id}/pairings/random")
    public ResponseEntity<?> generateRandomPairings(@PathVariable UUID id) {
        try {
            List<MatchResponse> matches = matchService.generateRandomPairings(id);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
