package com.torneo.mtg.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class GoogleAuthRequest(
    val idToken: String,
    val email: String,
    val name: String
)

data class AuthResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val eloRating: Int,
    val token: String
)

data class CreateTournamentRequest(
    val name: String,
    val format: String = "MODERN",
    val totalRounds: Int = 3
)

data class TournamentResponse(
    val id: String,
    val name: String,
    val format: String,
    val status: String,
    val totalRounds: Int,
    val currentRound: Int,
    val participantsCount: Int,
    val createdAt: String
)

data class RegisterParticipantRequest(
    val name: String,
    val email: String? = null,
    val deckName: String? = null
)

data class ParticipantResponse(
    val id: String,
    val userId: String,
    val name: String,
    val email: String,
    val deckName: String?,
    val matchPoints: Int,
    val registeredAt: String
)

data class MatchPlayerResponse(
    val id: String,
    val userId: String,
    val name: String,
    val seatPosition: Int,
    val currentLife: Int,
    val poisonCounters: Int,
    val isEliminated: Boolean,
    val score: Int
)

data class MatchResponse(
    val id: String,
    val tournamentId: String,
    val tableNumber: Int,
    val roundNumber: Int,
    val status: String,
    val winnerId: String?,
    val winnerName: String?,
    val players: List<MatchPlayerResponse>
)

data class SubmitResultRequest(
    val winnerId: String?,
    val scores: List<PlayerScoreDto>
)

data class PlayerScoreDto(
    val userId: String,
    val score: Int
)

data class UpdateLifeRequest(
    val userId: String,
    val lifeChange: Int,
    val poisonChange: Int
)
