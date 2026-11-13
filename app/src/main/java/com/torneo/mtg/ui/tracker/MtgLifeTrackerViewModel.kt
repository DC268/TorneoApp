package com.torneo.mtg.ui.tracker

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.torneo.mtg.domain.model.MtgFormat
import com.torneo.mtg.domain.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LifeTrackerState(
    val format: MtgFormat = MtgFormat.COMMANDER,
    val playerCount: Int = 2,
    val players: List<PlayerState> = emptyList(),
    val isSetupFinished: Boolean = false, // Inicia en FALSE para no saltarse la configuración
    val activePlayerId: Int? = null,
    val isAnyTimerRunning: Boolean = false
)

class MtgLifeTrackerViewModel : ViewModel() {

    private val playerColors = listOf(
        Color(0xFFE53935), // Rojo - Jugador 1
        Color(0xFF1E88E5), // Azul - Jugador 2
        Color(0xFF43A047), // Verde - Jugador 3
        Color(0xFF8E24AA)  // Morado - Jugador 4
    )

    private val _uiState = MutableStateFlow(LifeTrackerState())
    val uiState: StateFlow<LifeTrackerState> = _uiState.asStateFlow()

    private var timerLoopJob: Job? = null

    // NOTA: No llamamos a initializeGame() en init{} para que la pantalla de configuración
    // para 2, 3 o 4 jugadores SIEMPRE se muestre primero al usuario.

    fun initializeGame(playerCount: Int, format: MtgFormat, minutesPerPlayer: Int = 20) {
        val totalSeconds = minutesPerPlayer * 60
        val players = (1..playerCount).map { index ->
            PlayerState(
                id = index,
                name = "Jugador $index",
                color = playerColors[(index - 1) % playerColors.size],
                life = format.startingLife,
                timeSeconds = totalSeconds,
                isTimerRunning = false
            )
        }

        stopTimerLoop()

        _uiState.update {
            it.copy(
                format = format,
                playerCount = playerCount,
                players = players,
                isSetupFinished = true,
                activePlayerId = null,
                isAnyTimerRunning = false
            )
        }
    }

    // --- Control del Reloj Individual (20 Minutos por Jugador) ---

    fun togglePlayerTimer(playerId: Int) {
        val currentActive = _uiState.value.activePlayerId
        if (currentActive == playerId && _uiState.value.isAnyTimerRunning) {
            // Pausar
            pauseAllTimers()
        } else {
            // Iniciar turno para este jugador y pausar a los demás (estilo reloj de ajedrez)
            startTimerForPlayer(playerId)
        }
    }

    fun passTurn() {
        val state = _uiState.value
        val players = state.players
        if (players.isEmpty()) return

        val currentId = state.activePlayerId ?: players.first().id
        val currentIndex = players.indexOfFirst { it.id == currentId }

        // Encontrar el siguiente jugador vivo
        for (i in 1..players.size) {
            val nextIndex = (currentIndex + i) % players.size
            val candidate = players[nextIndex]
            if (!candidate.isDead) {
                startTimerForPlayer(candidate.id)
                return
            }
        }
    }

    private fun startTimerForPlayer(playerId: Int) {
        _uiState.update { state ->
            val updated = state.players.map { p ->
                p.copy(isTimerRunning = p.id == playerId && !p.isDead)
            }
            state.copy(
                players = updated,
                activePlayerId = playerId,
                isAnyTimerRunning = true
            )
        }
        startTimerLoop()
    }

    fun pauseAllTimers() {
        _uiState.update { state ->
            val updated = state.players.map { it.copy(isTimerRunning = false) }
            state.copy(players = updated, isAnyTimerRunning = false)
        }
        stopTimerLoop()
    }

    fun toggleGlobalTimer() {
        if (_uiState.value.isAnyTimerRunning) {
            pauseAllTimers()
        } else {
            val targetId = _uiState.value.activePlayerId ?: _uiState.value.players.firstOrNull()?.id
            targetId?.let { startTimerForPlayer(it) }
        }
    }

    private fun startTimerLoop() {
        if (timerLoopJob?.isActive == true) return
        timerLoopJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentState = _uiState.value
                val activeId = currentState.activePlayerId
                if (activeId != null && currentState.isAnyTimerRunning) {
                    var shouldAutoPass = false
                    _uiState.update { state ->
                        val updated = state.players.map { player ->
                            if (player.id == activeId && player.isTimerRunning) {
                                val remaining = player.timeSeconds - 1
                                if (remaining <= 0) {
                                    shouldAutoPass = true
                                    player.copy(timeSeconds = 0, isTimerRunning = false)
                                } else {
                                    player.copy(timeSeconds = remaining)
                                }
                            } else {
                                player
                            }
                        }
                        state.copy(players = updated)
                    }
                    if (shouldAutoPass) {
                        passTurn()
                    }
                }
            }
        }
    }

    private fun stopTimerLoop() {
        timerLoopJob?.cancel()
        timerLoopJob = null
    }

    // --- Control de Vidas, Veneno y Daño de Comandante ---

    fun modifyLife(playerId: Int, delta: Int) {
        _uiState.update { state ->
            val updated = state.players.map { player ->
                if (player.id == playerId) {
                    player.copy(life = player.life + delta)
                } else player
            }
            state.copy(players = updated)
        }
    }

    fun modifyPoison(playerId: Int, delta: Int) {
        _uiState.update { state ->
            val updated = state.players.map { player ->
                if (player.id == playerId) {
                    player.copy(poison = (player.poison + delta).coerceAtLeast(0))
                } else player
            }
            state.copy(players = updated)
        }
    }

    fun addCommanderDamage(victimId: Int, attackerId: Int, delta: Int) {
        _uiState.update { state ->
            val updated = state.players.map { player ->
                if (player.id == victimId) {
                    val currentDmg = player.commanderDamage[attackerId] ?: 0
                    val newDmg = (currentDmg + delta).coerceAtLeast(0)
                    val newMap = player.commanderDamage.toMutableMap().apply { put(attackerId, newDmg) }
                    // En MTG el daño de comandante también reduce los puntos de vida
                    player.copy(
                        life = player.life - delta,
                        commanderDamage = newMap
                    )
                } else player
            }
            state.copy(players = updated)
        }
    }

    fun resetMatch() {
        val currentCount = _uiState.value.playerCount
        val currentFormat = _uiState.value.format
        initializeGame(currentCount, currentFormat, 20)
    }

    fun openSetup() {
        pauseAllTimers()
        _uiState.update { it.copy(isSetupFinished = false) }
    }
}
