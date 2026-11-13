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
    val commanderDamage: Map<Int, Int> = emptyMap()
) {
    val isDead: Boolean
        get() = life <= 0 || poison >= 10 || commanderDamage.values.any { it >= 21 }

    val deathReason: String?
        get() = when {
            life <= 0 -> "0 Vidas"
            poison >= 10 -> "Infect / Veneno (10+)"
            commanderDamage.values.any { it >= 21 } -> "Daño Comandante (21+)"
            else -> null
        }
}
