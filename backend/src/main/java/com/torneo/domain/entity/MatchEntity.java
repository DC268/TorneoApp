package com.torneo.domain.entity;

import com.torneo.domain.model.MatchStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber = 1;

    @Column(name = "round_name", length = 100)
    private String roundName = "Ronda 1";

    @Column(name = "match_number", nullable = false)
    private Integer matchNumber = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_a_id")
    private ParticipantEntity participantA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_b_id")
    private ParticipantEntity participantB;

    @Column(name = "score_a")
    private Integer scoreA;

    @Column(name = "score_b")
    private Integer scoreB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private ParticipantEntity winner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchStatus status = MatchStatus.PENDING;

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    public MatchEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TournamentEntity getTournament() { return tournament; }
    public void setTournament(TournamentEntity tournament) { this.tournament = tournament; }

    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }

    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }

    public Integer getMatchNumber() { return matchNumber; }
    public void setMatchNumber(Integer matchNumber) { this.matchNumber = matchNumber; }

    public ParticipantEntity getParticipantA() { return participantA; }
    public void setParticipantA(ParticipantEntity participantA) { this.participantA = participantA; }

    public ParticipantEntity getParticipantB() { return participantB; }
    public void setParticipantB(ParticipantEntity participantB) { this.participantB = participantB; }

    public Integer getScoreA() { return scoreA; }
    public void setScoreA(Integer scoreA) { this.scoreA = scoreA; }

    public Integer getScoreB() { return scoreB; }
    public void setScoreB(Integer scoreB) { this.scoreB = scoreB; }

    public ParticipantEntity getWinner() { return winner; }
    public void setWinner(ParticipantEntity winner) { this.winner = winner; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
