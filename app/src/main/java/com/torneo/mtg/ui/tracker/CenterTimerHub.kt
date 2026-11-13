package com.torneo.mtg.ui.tracker

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CenterTimerHub(
    modifier: Modifier = Modifier,
    isAnyRunning: Boolean,
    onToggleGlobalTimer: () -> Unit,
    onPassTurn: () -> Unit,
    onResetMatch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        modifier = modifier
            .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF151515).copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Botón Pausa / Reanudar Global
            IconButton(onClick = onToggleGlobalTimer, modifier = Modifier.size(34.dp)) {
                Text(
                    text = if (isAnyRunning) "⏸" else "▶",
                    color = Color.White,
                    fontSize = 15.sp
                )
            }

            // Botón Pasar Turno (Avanza reloj al siguiente jugador)
            IconButton(onClick = onPassTurn, modifier = Modifier.size(34.dp)) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Pasar Turno",
                    tint = Color(0xFF34D399),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Botón Reiniciar
            IconButton(onClick = onResetMatch, modifier = Modifier.size(34.dp)) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reiniciar Vidas",
                    tint = Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Botón Configuración (2, 3 o 4 Jugadores / Formato)
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(34.dp)) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Configuración Jugadores",
                    tint = Color(0xFF818CF8),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
