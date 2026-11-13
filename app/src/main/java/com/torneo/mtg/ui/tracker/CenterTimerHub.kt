package com.torneo.mtg.ui.tracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.torneo.mtg.domain.model.PlayerState

@Composable
fun CenterTimerHub(
    modifier: Modifier = Modifier,
    players: List<PlayerState>,
    isAnyRunning: Boolean,
    onTogglePlayerTimer: (playerId: Int) -> Unit,
    onToggleGlobalTimer: () -> Unit,
    onPassTurn: () -> Unit,
    onResetMatch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    when (players.size) {
        2 -> TwoPlayersCenterHub(
            modifier = modifier,
            p1 = players[0],
            p2 = players[1],
            isAnyRunning = isAnyRunning,
            onTogglePlayerTimer = onTogglePlayerTimer,
            onToggleGlobalTimer = onToggleGlobalTimer,
            onPassTurn = onPassTurn,
            onResetMatch = onResetMatch,
            onOpenSettings = onOpenSettings
        )
        3 -> ThreePlayersCenterHub(
            modifier = modifier,
            players = players,
            isAnyRunning = isAnyRunning,
            onTogglePlayerTimer = onTogglePlayerTimer,
            onToggleGlobalTimer = onToggleGlobalTimer,
            onPassTurn = onPassTurn,
            onResetMatch = onResetMatch,
            onOpenSettings = onOpenSettings
        )
        else -> FourPlayersCenterHub(
            modifier = modifier,
            players = players,
            isAnyRunning = isAnyRunning,
            onTogglePlayerTimer = onTogglePlayerTimer,
            onToggleGlobalTimer = onToggleGlobalTimer,
            onPassTurn = onPassTurn,
            onResetMatch = onResetMatch,
            onOpenSettings = onOpenSettings
        )
    }
}

// =========================================================================
// HUB CENTRAL PARA 2 JUGADORES (En la mitad de la pantalla con su color)
// =========================================================================
@Composable
fun TwoPlayersCenterHub(
    modifier: Modifier = Modifier,
    p1: PlayerState,
    p2: PlayerState,
    isAnyRunning: Boolean,
    onTogglePlayerTimer: (Int) -> Unit,
    onToggleGlobalTimer: () -> Unit,
    onPassTurn: () -> Unit,
    onResetMatch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Reloj Jugador 2 (Invertido 180° hacia su lado de la mesa, con su color)
        PlayerTimerCard(
            player = p2,
            isRotated = true,
            onClick = { onTogglePlayerTimer(p2.id) },
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(64.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Barra de Control Central
        CenterControlsRow(
            isAnyRunning = isAnyRunning,
            onToggleGlobalTimer = onToggleGlobalTimer,
            onPassTurn = onPassTurn,
            onResetMatch = onResetMatch,
            onOpenSettings = onOpenSettings
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Reloj Jugador 1 (Hacia el lado inferior, con su color)
        PlayerTimerCard(
            player = p1,
            isRotated = false,
            onClick = { onTogglePlayerTimer(p1.id) },
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(64.dp)
        )
    }
}

// =========================================================================
// HUB CENTRAL PARA 3 JUGADORES
// =========================================================================
@Composable
fun ThreePlayersCenterHub(
    modifier: Modifier = Modifier,
    players: List<PlayerState>,
    isAnyRunning: Boolean,
    onTogglePlayerTimer: (Int) -> Unit,
    onToggleGlobalTimer: () -> Unit,
    onPassTurn: () -> Unit,
    onResetMatch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila Superior (P2 y P3 invertidos hacia arriba)
        Row(
            modifier = Modifier.fillMaxWidth(0.92f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (players.size > 1) {
                PlayerTimerCard(
                    player = players[1],
                    isRotated = true,
                    onClick = { onTogglePlayerTimer(players[1].id) },
                    modifier = Modifier.weight(1f).height(58.dp)
                )
            }
            if (players.size > 2) {
                PlayerTimerCard(
                    player = players[2],
                    isRotated = true,
                    onClick = { onTogglePlayerTimer(players[2].id) },
                    modifier = Modifier.weight(1f).height(58.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        CenterControlsRow(
            isAnyRunning = isAnyRunning,
            onToggleGlobalTimer = onToggleGlobalTimer,
            onPassTurn = onPassTurn,
            onResetMatch = onResetMatch,
            onOpenSettings = onOpenSettings
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Jugador 1 abajo
        if (players.isNotEmpty()) {
            PlayerTimerCard(
                player = players[0],
                isRotated = false,
                onClick = { onTogglePlayerTimer(players[0].id) },
                modifier = Modifier.fillMaxWidth(0.6f).height(58.dp)
            )
        }
    }
}

// =========================================================================
// HUB CENTRAL PARA 4 JUGADORES
// =========================================================================
@Composable
fun FourPlayersCenterHub(
    modifier: Modifier = Modifier,
    players: List<PlayerState>,
    isAnyRunning: Boolean,
    onTogglePlayerTimer: (Int) -> Unit,
    onToggleGlobalTimer: () -> Unit,
    onPassTurn: () -> Unit,
    onResetMatch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila Superior (P3 y P4 invertidos)
        Row(
            modifier = Modifier.fillMaxWidth(0.95f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (players.size > 2) {
                PlayerTimerCard(
                    player = players[2],
                    isRotated = true,
                    onClick = { onTogglePlayerTimer(players[2].id) },
                    modifier = Modifier.weight(1f).height(54.dp)
                )
            }
            if (players.size > 3) {
                PlayerTimerCard(
                    player = players[3],
                    isRotated = true,
                    onClick = { onTogglePlayerTimer(players[3].id) },
                    modifier = Modifier.weight(1f).height(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        CenterControlsRow(
            isAnyRunning = isAnyRunning,
            onToggleGlobalTimer = onToggleGlobalTimer,
            onPassTurn = onPassTurn,
            onResetMatch = onResetMatch,
            onOpenSettings = onOpenSettings
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Fila Inferior (P1 y P2)
        Row(
            modifier = Modifier.fillMaxWidth(0.95f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (players.isNotEmpty()) {
                PlayerTimerCard(
                    player = players[0],
                    isRotated = false,
                    onClick = { onTogglePlayerTimer(players[0].id) },
                    modifier = Modifier.weight(1f).height(54.dp)
                )
            }
            if (players.size > 1) {
                PlayerTimerCard(
                    player = players[1],
                    isRotated = false,
                    onClick = { onTogglePlayerTimer(players[1].id) },
                    modifier = Modifier.weight(1f).height(54.dp)
                )
            }
        }
    }
}

// =========================================================================
// TARJETA DE TIEMPO INDIVIDUAL (ESTILO VIDA GRANDE CON SU COLOR)
// =========================================================================
@Composable
fun PlayerTimerCard(
    player: PlayerState,
    isRotated: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = player.isTimerRunning
    val isWarning = player.timeSeconds in 1..180
    val isExpired = player.timeSeconds <= 0

    Surface(
        onClick = onClick,
        color = player.color.copy(alpha = if (isRunning) 0.95f else 0.75f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            if (isRunning) 3.dp else 1.5.dp,
            if (isRunning) Color(0xFF34D399) // Verde vivo cuando es su turno
            else if (isWarning) Color(0xFFFFCC00)
            else if (isExpired) Color.Red
            else Color.White.copy(alpha = 0.35f)
        ),
        shadowElevation = if (isRunning) 8.dp else 3.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .then(if (isRotated) Modifier.rotate(180f) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Nombre y estado de turno
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = player.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isRunning) "▶ EN TURNO" else "⏸ PAUSADO",
                        color = if (isRunning) Color(0xFF6EE7B7) else Color.White.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // TIEMPO EN GRANDE COMO VIDAS
                Text(
                    text = player.formattedTime,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isWarning) Color(0xFFFFEB3B) else Color.White
                )
            }
        }
    }
}

// =========================================================================
// BOTONERA DE CONTROLES CENTRALES
// =========================================================================
@Composable
fun CenterControlsRow(
    isAnyRunning: Boolean,
    onToggleGlobalTimer: () -> Unit,
    onPassTurn: () -> Unit,
    onResetMatch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF111827).copy(alpha = 0.96f),
        border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.25f)),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Pausa / Reanudar Global
            IconButton(onClick = onToggleGlobalTimer, modifier = Modifier.size(32.dp)) {
                Text(
                    text = if (isAnyRunning) "⏸" else "▶",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            // Pasar Turno
            IconButton(onClick = onPassTurn, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Pasar Turno",
                    tint = Color(0xFF34D399),
                    modifier = Modifier.size(19.dp)
                )
            }

            // Reiniciar Partida
            IconButton(onClick = onResetMatch, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reiniciar",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Configuración
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Ajustes",
                    tint = Color(0xFF818CF8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
