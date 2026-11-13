package com.torneo.mtg.domain.model

import androidx.compose.ui.graphics.Color

enum class MtgFormat(val displayName: String, val startingLife: Int) {
    STANDARD("Estándar", 20),
    COMMANDER("Commander", 40)
}

data class PlayerState(
    val id: Int,
    val name: String,
    val color: Color,
    val life: Int,
    val poison: Int = 0,
    val commanderDamage: Map<Int, Int> = emptyMap(),
    val timeSeconds: Int = 20 * 60, // 20 minutos por jugador
    val isTimerRunning: Boolean = false
) {
    val isDead: Boolean
        get() = life <= 0 || poison >= 10 || commanderDamage.values.any { it >= 21 } || timeSeconds <= 0

    val deathReason: String?
        get() = when {
            timeSeconds <= 0 -> "Tiempo Agotado (00:00)"
            life <= 0 -> "0 Vidas"
            poison >= 10 -> "Infect / Veneno (10+)"
            commanderDamage.values.any { it >= 21 } -> "Daño Comandante (21+)"
            else -> null
        }

    val formattedTime: String
        get() {
            val m = timeSeconds / 60
            val s = timeSeconds % 60
            return "%02d:%02d".format(m, s)
        }
}
