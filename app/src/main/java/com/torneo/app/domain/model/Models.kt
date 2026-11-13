package com.torneo.app.domain.model

enum class Role {
    ADMIN,
    CLIENT
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val avatarUrl: String? = null
)

data class Tournament(
    val id: String,
    val name: String,
    val description: String?,
    val category: String?,
    val status: String,
    val participantsCount: Int,
    val matchesCount: Int
)

data class Participant(
    val id: String,
    val tournamentId: String,
    val name: String,
    val email: String,
    val phone: String?,
    val identification: String?,
    val category: String?,
    val seedNumber: Int?,
    val status: String
)

data class Match(
    val id: String,
    val tournamentId: String,
    val roundNumber: Int,
    val roundName: String,
    val matchNumber: Int,
    val participantA: Participant?,
    val participantB: Participant?,
    val scoreA: Int?,
    val scoreB: Int?,
    val winner: Participant?,
    val status: String,
    val notes: String?
)
