package com.torneo.app.data.repository

import android.content.Context
import com.torneo.app.data.api.ApiClient
import com.torneo.app.data.dto.*
import com.torneo.app.domain.model.Match
import com.torneo.app.domain.model.Participant
import com.torneo.app.domain.model.Tournament
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TournamentRepository(private val context: Context) {

    private val api = ApiClient.getService(context)

    suspend fun getTournaments(): Result<List<Tournament>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTournaments()
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!.map {
                    Tournament(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        category = it.category,
                        status = it.status,
                        participantsCount = it.participantsCount,
                        matchesCount = it.matchesCount
                    )
                }
                Result.success(list)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error obteniendo torneos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerParticipant(
        tournamentId: String,
        name: String,
        email: String,
        phone: String?,
        identification: String?,
        category: String?
    ): Result<Participant> = withContext(Dispatchers.IO) {
        try {
            val req = ParticipantCreateRequestDto(tournamentId, name, email, phone, identification, category)
            val response = api.registerParticipant(req)
            if (response.isSuccessful && response.body() != null) {
                val it = response.body()!!
                Result.success(
                    Participant(
                        id = it.id,
                        tournamentId = it.tournamentId ?: tournamentId,
                        name = it.name,
                        email = it.email,
                        phone = it.phone,
                        identification = it.identification,
                        category = it.category,
                        seedNumber = it.seedNumber,
                        status = it.status
                    )
                )
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al inscribir participante"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getParticipants(tournamentId: String): Result<List<Participant>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getParticipantsByTournament(tournamentId)
            if (response.isSuccessful && response.body() != null) {
                val list = response.body()!!.map {
                    Participant(
                        id = it.id,
                        tournamentId = it.tournamentId ?: tournamentId,
                        name = it.name,
                        email = it.email,
                        phone = it.phone,
                        identification = it.identification,
                        category = it.category,
                        seedNumber = it.seedNumber,
                        status = it.status
                    )
                }
                Result.success(list)
            } else {
                Result.failure(Exception("Error obteniendo participantes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteParticipant(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteParticipant(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error al eliminar participante"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateRandomMatchmaking(tournamentId: String): Result<List<Match>> = withContext(Dispatchers.IO) {
        try {
            val response = api.generateMatchmaking(MatchmakingRequestDto(tournamentId, true))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { mapMatchDto(it) })
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error en emparejamiento"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMatches(tournamentId: String): Result<List<Match>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMatchesByTournament(tournamentId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { mapMatchDto(it) })
            } else {
                Result.failure(Exception("Error al obtener encuentros"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatchResult(
        matchId: String,
        scoreA: Int?,
        scoreB: Int?,
        winnerId: String?,
        notes: String?
    ): Result<Match> = withContext(Dispatchers.IO) {
        try {
            val req = MatchUpdateRequestDto(scoreA, scoreB, winnerId, if (winnerId != null) "COMPLETED" else "IN_PROGRESS", notes)
            val response = api.updateMatchResult(matchId, req)
            if (response.isSuccessful && response.body() != null) {
                Result.success(mapMatchDto(response.body()!!))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error al actualizar resultado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapMatchDto(dto: MatchResponseDto): Match {
        return Match(
            id = dto.id,
            tournamentId = dto.tournamentId,
            roundNumber = dto.roundNumber,
            roundName = dto.roundName,
            matchNumber = dto.matchNumber,
            participantA = dto.participantA?.let { mapParticipantDto(it) },
            participantB = dto.participantB?.let { mapParticipantDto(it) },
            scoreA = dto.scoreA,
            scoreB = dto.scoreB,
            winner = dto.winner?.let { mapParticipantDto(it) },
            status = dto.status,
            notes = dto.notes
        )
    }

    private fun mapParticipantDto(dto: ParticipantResponseDto): Participant {
        return Participant(
            id = dto.id,
            tournamentId = dto.tournamentId ?: "",
            name = dto.name,
            email = dto.email,
            phone = dto.phone,
            identification = dto.identification,
            category = dto.category,
            seedNumber = dto.seedNumber,
            status = dto.status
        )
    }
}
