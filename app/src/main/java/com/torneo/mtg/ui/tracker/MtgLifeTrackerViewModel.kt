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
    val playerCount: Int = 4,
    val players: List<PlayerState> = emptyList(),
    val isSetupFinished: Boolean = false,
    val timerSeconds: Int = 50 * 60,
    val isTimerRunning: Boolean = false
)

class MtgLifeTrackerViewModel : ViewModel() {

    private val playerColors = listOf(
        Color(0xFFE53935), // Rojo
        Color(0xFF1E88E5), // Azul
        Color(0xFF43A047), // Verde
        Color(0xFF8E24AA)  // Morado
    )

    private val _uiState = MutableStateFlow(LifeTrackerState())
    val uiState: StateFlow<LifeTrackerState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        initializeGame(4, MtgFormat.COMMANDER)
    }

    fun initializeGame(playerCount: Int, format: MtgFormat) {
        val players = (1..playerCount).map { index ->
            PlayerState(
                id = index,
                name = "Jugador $index",
                color = playerColors[(index - 1) % playerColors.size],
                life = format.startingLife
            )
        }

        _uiState.update {
            it.copy(
                format = format,
                playerCount = playerCount,
                players = players,
                isSetupFinished = true,
                timerSeconds = 50 * 60,
                isTimerRunning = false
            )
        }
        pauseTimer()
    }

    fun modifyLife(playerId: Int, delta: Int) {
        _uiState.update { state ->
            val updated = state.players.map { player ->
                if (player.id == playerId) player.copy(life = player.life + delta)
                else player
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
                    player.copy(
                        life = player.life - delta,
                        commanderDamage = newMap
                    )
                } else player
            }
            state.copy(players = updated)
        }
    }

    fun toggleTimer() {
        if (_uiState.value.isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _uiState.update { it.copy(isTimerRunning = true) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isTimerRunning && _uiState.value.timerSeconds > 0) {
                delay(1000L)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            if (_uiState.value.timerSeconds <= 0) {
                _uiState.update { it.copy(isTimerRunning = false) }
            }
        }
    }

    fun pauseTimer() {
        _uiState.update { it.copy(isTimerRunning = false) }
        timerJob?.cancel()
    }

    fun resetTimer(minutes: Int = 50) {
        pauseTimer()
        _uiState.update { it.copy(timerSeconds = minutes * 60) }
    }

    fun resetGame() {
        initializeGame(_uiState.value.playerCount, _uiState.value.format)
    }

    fun openSetup() {
        _uiState.update { it.copy(isSetupFinished = false) }
    }
}
