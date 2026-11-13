package com.torneo.mtg.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "match_players")
public class MatchPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    @JsonIgnore
    private Match match;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int seatPosition;
    private int currentLife = 20;
    private int poisonCounters = 0;
    private boolean isEliminated = false;
    private int score = 0;

    public MatchPlayer() {}

    public MatchPlayer(Match match, User user, int seatPosition, int currentLife) {
        this.match = match;
        this.user = user;
        this.seatPosition = seatPosition;
        this.currentLife = currentLife;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getSeatPosition() { return seatPosition; }
    public void setSeatPosition(int seatPosition) { this.seatPosition = seatPosition; }

    public int getCurrentLife() { return currentLife; }
    public void setCurrentLife(int currentLife) { this.currentLife = currentLife; }

    public int getPoisonCounters() { return poisonCounters; }
    public void setPoisonCounters(int poisonCounters) { this.poisonCounters = poisonCounters; }

    public boolean isEliminated() { return isEliminated; }
    public void setEliminated(boolean eliminated) { isEliminated = eliminated; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
