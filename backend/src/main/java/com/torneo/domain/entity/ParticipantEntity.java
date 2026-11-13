package com.torneo.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "participants")
public class ParticipantEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String identification;

    @Column(length = 100)
    private String category;

    @Column(name = "seed_number")
    private Integer seedNumber;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public ParticipantEntity() {}

    public ParticipantEntity(String id, String name, String email, String phone, String identification, String category, TournamentEntity tournament) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.identification = identification;
        this.category = category;
        this.tournament = tournament;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdentification() { return identification; }
    public void setIdentification(String identification) { this.identification = identification; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getSeedNumber() { return seedNumber; }
    public void setSeedNumber(Integer seedNumber) { this.seedNumber = seedNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public TournamentEntity getTournament() { return tournament; }
    public void setTournament(TournamentEntity tournament) { this.tournament = tournament; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
