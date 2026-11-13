package com.torneo.mtg.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    private int tableNumber;
    private int roundNumber = 1;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private User winner;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MatchPlayer> players = new ArrayList<>();

    private Instant createdAt = Instant.now();

    public Match() {}

    public Match(Tournament tournament, int tableNumber, int roundNumber) {
        this.tournament = tournament;
        this.tableNumber = tableNumber;
        this.roundNumber = roundNumber;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }

    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getWinner() { return winner; }
    public void setWinner(User winner) { this.winner = winner; }

    public List<MatchPlayer> getPlayers() { return players; }
    public void setPlayers(List<MatchPlayer> players) { this.players = players; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
