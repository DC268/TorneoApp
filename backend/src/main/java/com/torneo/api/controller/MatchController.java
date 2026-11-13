package com.torneo.api.controller;

import com.torneo.api.dto.MatchResponse;
import com.torneo.api.dto.MatchUpdateRequest;
import com.torneo.api.dto.MatchmakingRequest;
import com.torneo.application.service.MatchmakingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchmakingService matchmakingService;

    public MatchController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    /**
     * Pestaña 2 del Administrador: Emparejar automáticamente y de forma random
     */
    @PostMapping("/matchmaking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatchResponse>> generateMatchmaking(@Valid @RequestBody MatchmakingRequest request) {
        List<MatchResponse> matches = matchmakingService.generateRandomMatchmaking(request);
        return ResponseEntity.ok(matches);
    }

    /**
     * Pestaña 3 del Administrador & Vista de Resultados: Ver los resultados de los encuentros
     */
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<MatchResponse>> getMatchesByTournament(@PathVariable String tournamentId) {
        return ResponseEntity.ok(matchmakingService.getMatchesByTournament(tournamentId));
    }

    /**
     * Pestaña 3 del Administrador: Actualizar resultado de un encuentro
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchResponse> updateMatchResult(
            @PathVariable String id,
            @RequestBody MatchUpdateRequest request
    ) {
        MatchResponse response = matchmakingService.updateMatchResult(id, request);
        return ResponseEntity.ok(response);
    }
}
