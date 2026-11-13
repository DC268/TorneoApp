package com.torneo.mtg.data.network

import com.torneo.mtg.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse>

    @POST("api/auth/google")
    suspend fun googleAuth(@Body req: GoogleAuthRequest): Response<AuthResponse>

    @GET("api/tournaments")
    suspend fun getTournaments(): Response<List<TournamentResponse>>

    @POST("api/tournaments")
    suspend fun createTournament(@Body req: CreateTournamentRequest): Response<TournamentResponse>

    // Pestaña 1: Inscribir
    @GET("api/tournaments/{id}/participants")
    suspend fun getParticipants(@Path("id") tournamentId: String): Response<List<ParticipantResponse>>

    @POST("api/tournaments/{id}/register")
    suspend fun registerParticipant(
        @Path("id") tournamentId: String,
        @Body req: RegisterParticipantRequest
    ): Response<ParticipantResponse>

    @DELETE("api/tournaments/{id}/participants/{userId}")
    suspend fun removeParticipant(
        @Path("id") tournamentId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    // Pestaña 2: Emparejar de forma random
    @POST("api/tournaments/{id}/pairings/random")
    suspend fun generateRandomPairings(@Path("id") tournamentId: String): Response<List<MatchResponse>>

    // Pestaña 3: Resultados
    @GET("api/matches/tournament/{tournamentId}")
    suspend fun getMatches(@Path("tournamentId") tournamentId: String): Response<List<MatchResponse>>

    @POST("api/matches/{matchId}/results")
    suspend fun submitResult(
        @Path("matchId") matchId: String,
        @Body req: SubmitResultRequest
    ): Response<MatchResponse>

    @POST("api/matches/{matchId}/life")
    suspend fun updateLife(
        @Path("matchId") matchId: String,
        @Body req: UpdateLifeRequest
    ): Response<MatchResponse>
}
