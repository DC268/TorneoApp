package com.torneo.app.data.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: String? = "CLIENT"
)

data class GoogleLoginRequestDto(
    val idToken: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val avatarUrl: String?
)

data class AuthResponseDto(
    val token: String,
    val user: UserDto
)

data class TournamentDto(
    val id: String,
    val name: String,
    val description: String?,
    val category: String?,
    val status: String,
    val participantsCount: Int,
    val matchesCount: Int
)

data class TournamentCreateRequestDto(
    val name: String,
    val description: String?,
    val category: String?
)

data class ParticipantCreateRequestDto(
    val tournamentId: String,
    val name: String,
    val email: String,
    val phone: String?,
    val identification: String?,
    val category: String?
)

data class ParticipantResponseDto(
    val id: String,
    val tournamentId: String?,
    val name: String,
    val email: String,
    val phone: String?,
    val identification: String?,
    val category: String?,
    val seedNumber: Int?,
    val status: String
)

data class MatchmakingRequestDto(
    val tournamentId: String,
    val reshuffleExisting: Boolean = true
)

data class MatchResponseDto(
    val id: String,
    val tournamentId: String,
    val roundNumber: Int,
    val roundName: String,
    val matchNumber: Int,
    val participantA: ParticipantResponseDto?,
    val participantB: ParticipantResponseDto?,
    val scoreA: Int?,
    val scoreB: Int?,
    val winner: ParticipantResponseDto?,
    val status: String,
    val notes: String?
)

data class MatchUpdateRequestDto(
    val scoreA: Int?,
    val scoreB: Int?,
    val winnerId: String?,
    val status: String?,
    val notes: String?
)
