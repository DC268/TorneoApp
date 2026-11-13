package com.torneo.app.data.api

import com.torneo.app.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface TorneoApiService {

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("api/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequestDto): Response<AuthResponseDto>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<UserDto>

    // Tournaments
    @GET("api/tournaments")
    suspend fun getTournaments(): Response<List<TournamentDto>>

    @GET("api/tournaments/{id}")
    suspend fun getTournamentById(@Path("id") id: String): Response<TournamentDto>

    @POST("api/tournaments")
    suspend fun createTournament(@Body request: TournamentCreateRequestDto): Response<TournamentDto>

    // Pestaña 1: Inscribir personas a un torneo
    @POST("api/participants")
    suspend fun registerParticipant(@Body request: ParticipantCreateRequestDto): Response<ParticipantResponseDto>

    @GET("api/participants/tournament/{tournamentId}")
    suspend fun getParticipantsByTournament(@Path("tournamentId") tournamentId: String): Response<List<ParticipantResponseDto>>

    @DELETE("api/participants/{id}")
    suspend fun deleteParticipant(@Path("id") id: String): Response<Unit>

    // Pestaña 2: Emparejar automáticamente y de forma random
    @POST("api/matches/matchmaking")
    suspend fun generateMatchmaking(@Body request: MatchmakingRequestDto): Response<List<MatchResponseDto>>

    // Pestaña 3: Resultados de los encuentros
    @GET("api/matches/tournament/{tournamentId}")
    suspend fun getMatchesByTournament(@Path("tournamentId") tournamentId: String): Response<List<MatchResponseDto>>

    @PUT("api/matches/{id}")
    suspend fun updateMatchResult(@Path("id") id: String, @Body request: MatchUpdateRequestDto): Response<MatchResponseDto>
}
