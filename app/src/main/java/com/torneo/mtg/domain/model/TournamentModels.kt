package com.torneo.mtg.domain.model

enum class UserRole {
    ADMIN,
    PLAYER
}

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole = UserRole.PLAYER,
    val eloRating: Int = 1200
)

enum class TournamentStatus {
    PENDING,
    ACTIVE,
    FINISHED
}

data class Tournament(
    val id: String,
    val name: String,
    val format: String = "MODERN",
    val status: TournamentStatus = TournamentStatus.PENDING,
    val totalRounds: Int = 3,
    val currentRound: Int = 0,
    val participantsCount: Int = 0
)

enum class MatchStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class MatchPlayer(
    val id: String,
    val userId: String,
    val name: String,
    val seatPosition: Int,
    var currentLife: Int = 20,
    var poisonCounters: Int = 0,
    var score: Int = 0,
    var isEliminated: Boolean = false
)

data class Match(
    val id: String,
    val tournamentId: String,
    val tableNumber: Int,
    val roundNumber: Int,
    val status: MatchStatus = MatchStatus.PENDING,
    val players: List<MatchPlayer>,
    val winnerId: String? = null,
    val winnerName: String? = null
)
