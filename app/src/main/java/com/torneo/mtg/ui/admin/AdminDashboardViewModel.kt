package com.torneo.mtg.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.torneo.mtg.data.model.*
import com.torneo.mtg.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminDashboardState(
    val tournaments: List<TournamentResponse> = emptyList(),
    val selectedTournamentId: String? = null,
    val participants: List<ParticipantResponse> = emptyList(),
    val matches: List<MatchResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    init {
        loadTournaments()
    }

    fun loadTournaments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.getTournaments()
                if (res.isSuccessful && res.body() != null) {
                    val list = res.body()!!
                    val currentId = _state.value.selectedTournamentId ?: list.firstOrNull()?.id
                    _state.value = _state.value.copy(
                        tournaments = list,
                        selectedTournamentId = currentId,
                        isLoading = false
                    )
                    currentId?.let {
                        loadParticipants(it)
                        loadMatches(it)
                    }
                } else {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = "Error al cargar torneos")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    fun selectTournament(id: String) {
        _state.value = _state.value.copy(selectedTournamentId = id)
        loadParticipants(id)
        loadMatches(id)
    }

    fun loadParticipants(tournamentId: String) {
        viewModelScope.launch {
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.getParticipants(tournamentId)
                if (res.isSuccessful && res.body() != null) {
                    _state.value = _state.value.copy(participants = res.body()!!)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }

    // Pestaña 1: Inscribir persona
    fun registerParticipant(name: String, email: String, deck: String) {
        val tId = _state.value.selectedTournamentId ?: return
        viewModelScope.launch {
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.registerParticipant(tId, RegisterParticipantRequest(name, email, deck))
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(successMessage = "Participante inscrito con éxito")
                    loadParticipants(tId)
                } else {
                    _state.value = _state.value.copy(errorMessage = "Error al inscribir participante")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }

    fun removeParticipant(userId: String) {
        val tId = _state.value.selectedTournamentId ?: return
        viewModelScope.launch {
            try {
                val api = ApiClient.getService(getApplication())
                api.removeParticipant(tId, userId)
                loadParticipants(tId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }

    // Pestaña 2: Emparejar de forma random
    fun generateRandomPairings() {
        val tId = _state.value.selectedTournamentId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.generateRandomPairings(tId)
                if (res.isSuccessful && res.body() != null) {
                    _state.value = _state.value.copy(
                        matches = res.body()!!,
                        isLoading = false,
                        successMessage = "¡Emparejamiento aleatorio generado con éxito!"
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = "Se requieren al menos 2 participantes")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    // Pestaña 3: Resultados
    fun loadMatches(tournamentId: String) {
        viewModelScope.launch {
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.getMatches(tournamentId)
                if (res.isSuccessful && res.body() != null) {
                    _state.value = _state.value.copy(matches = res.body()!!)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }

    fun submitMatchResult(matchId: String, winnerId: String) {
        val tId = _state.value.selectedTournamentId ?: return
        viewModelScope.launch {
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.submitResult(
                    matchId,
                    SubmitResultRequest(winnerId, listOf(PlayerScoreDto(winnerId, 2)))
                )
                if (res.isSuccessful) {
                    loadMatches(tId)
                    loadParticipants(tId)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }
}
