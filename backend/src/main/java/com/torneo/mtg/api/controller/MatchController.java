package com.torneo.mtg.api.controller;

import com.torneo.mtg.api.dto.MatchDtos.*;
import com.torneo.mtg.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // Pestaña 3: Ver resultados de los encuentros
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<MatchResponse>> getMatchesByTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(matchService.getMatches(tournamentId));
    }

    @PostMapping("/{matchId}/results")
    public ResponseEntity<?> submitResult(
            @PathVariable UUID matchId,
            @RequestBody SubmitResultRequest req) {
        try {
            MatchResponse res = matchService.submitResult(matchId, req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // MTG Life Tracker live sync
    @PostMapping("/{matchId}/life")
    public ResponseEntity<?> updateLife(
            @PathVariable UUID matchId,
            @RequestBody UpdateLifeRequest req) {
        try {
            MatchResponse res = matchService.updateLife(matchId, req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
