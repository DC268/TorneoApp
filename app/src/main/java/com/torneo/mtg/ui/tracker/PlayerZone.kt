package com.torneo.mtg.ui.tracker

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.torneo.mtg.domain.model.PlayerState

@Composable
fun PlayerZone(
    player: PlayerState,
    allPlayers: List<PlayerState>,
    viewModel: MtgLifeTrackerViewModel
) {
    // Control de vista: false = Vidas y Tiempo; true = Daño de Comandante
    var showCommanderDamage by remember { mutableStateOf(false) }
    var totalDragX by remember { mutableFloatStateOf(0f) }

    val backgroundColor = if (player.isDead) Color(0xFF1E1E1E) else player.color.copy(alpha = 0.88f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
            .background(backgroundColor, RoundedCornerShape(14.dp))
            .border(
                BorderStroke(
                    if (player.isTimerRunning) 2.5.dp else 1.dp,
                    if (player.isTimerRunning) Color(0xFF34D399) else Color.White.copy(alpha = 0.25f)
                ),
                RoundedCornerShape(14.dp)
            )
            // Gesto: Deslizar a la derecha para ver daño de comandante; deslizar a la izquierda para volver
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { totalDragX = 0f },
                    onDragEnd = {
                        if (totalDragX > 45f) {
                            showCommanderDamage = true // Deslizó a la derecha -> muestra Daño de Comandante
                        } else if (totalDragX < -45f) {
                            showCommanderDamage = false // Deslizó a la izquierda -> vuelve a vidas
                        }
                        totalDragX = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount
                    }
                )
            }
    ) {
        // Indicador si el jugador perdió la partida
        if (player.isDead) {
            Surface(
                color = Color.Red.copy(alpha = 0.90f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Text("DERROTADO", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Text(player.deathReason ?: "", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Transición animada entre la vista de vidas y la de comandante
        AnimatedContent(
            targetState = showCommanderDamage,
            transitionSpec = {
                if (targetState) {
                    // Entra deslizándose a la derecha
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    // Regresa deslizándose a la izquierda
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            label = "PlayerZoneContent"
        ) { isCommanderView ->
            if (isCommanderView) {
                // ========================================================
                // VISTA 2: DAÑO DE COMANDANTE (AL DESLIZAR A LA DERECHA)
                // ========================================================
                CommanderDamageView(
                    player = player,
                    opponents = allPlayers.filter { it.id != player.id },
                    onAddDamage = { attackerId, delta ->
                        viewModel.addCommanderDamage(player.id, attackerId, delta)
                    },
                    onBackToLife = { showCommanderDamage = false }
                )
            } else {
                // ========================================================
                // VISTA 1: VIDAS Y TIEMPO (20 MIN) AL LADO DE LAS VIDAS
                // ========================================================
                LifeAndTimerView(
                    player = player,
                    viewModel = viewModel,
                    onOpenCommander = { showCommanderDamage = true }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// VISTA PRINCIPAL: VIDAS + RELOJ DE 20 MINUTOS AL LADO
// -------------------------------------------------------------
@Composable
fun LifeAndTimerView(
    player: PlayerState,
    viewModel: MtgLifeTrackerViewModel,
    onOpenCommander: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera: Nombre del jugador y estado de turno
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = player.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Indicador de deslizamiento hacia la derecha
            Surface(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { onOpenCommander() }
            ) {
                Text(
                    text = "👉 Cmd Dmg",
                    color = Color(0xFFFDE047),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Fila Central: VIDAS y TIEMPO (20 MINUTOS) AL LADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Botón (-) Vidas
            Button(
                onClick = { viewModel.modifyLife(player.id, -1) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.45f)),
                modifier = Modifier.size(52.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("-", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Total de Vidas
            Text(
                text = "${player.life}",
                fontSize = 46.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            // Botón (+) Vidas
            Button(
                onClick = { viewModel.modifyLife(player.id, 1) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.45f)),
                modifier = Modifier.size(52.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("+", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            // REQUISITO: RELOJ DE 20 MINUTOS AL LADO DE LAS VIDAS
            Surface(
                color = if (player.isTimerRunning) Color(0xFF065F46) else Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (player.isTimerRunning) Color(0xFF34D399)
                    else if (player.timeSeconds < 180) Color(0xFFF87171)
                    else Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable { viewModel.togglePlayerTimer(player.id) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = player.formattedTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (player.timeSeconds < 180) Color(0xFFFF5252) else Color.White
                    )
                    Text(
                        text = if (player.isTimerRunning) "TURNO ▶" else "PAUSA ⏸",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (player.isTimerRunning) Color(0xFF6EE7B7) else Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Fila Inferior: Sub-contadores y opciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contador de Veneno (Infect 10)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "-",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clickable { viewModel.modifyPoison(player.id, -1) }
                    )
                    Text(
                        text = "☣ ${player.poison}/10",
                        color = if (player.poison >= 10) Color.Green else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .clickable { viewModel.modifyPoison(player.id, 1) }
                    )
                }
            }

            // Resumen de daño de comandante recibido
            val maxCmdDmg = player.commanderDamage.values.maxOrNull() ?: 0
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.45f),
                modifier = Modifier.clickable { onOpenCommander() }
            ) {
                Text(
                    text = "👑 Cmd: $maxCmdDmg/21",
                    color = if (maxCmdDmg >= 21) Color(0xFFEF4444) else Color(0xFFFDE047),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// VISTA 2: PANEL DE DAÑO DE COMANDANTE (DESLIZANDO A LA DERECHA)
// -------------------------------------------------------------
@Composable
fun CommanderDamageView(
    player: PlayerState,
    opponents: List<PlayerState>,
    onAddDamage: (attackerId: Int, delta: Int) -> Unit,
    onBackToLife: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Cabecera con botón de retorno o deslizar a la izquierda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { onBackToLife() }
            ) {
                Text(
                    text = "👈 Volver a Vidas",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Text(
                text = "👑 Daño de Comandante",
                color = Color(0xFFFDE047),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Lista de oponentes para registrarles daño
        if (opponents.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No hay oponentes en mesa", color = Color.White, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(opponents) { opponent ->
                    val dmg = player.commanderDamage[opponent.id] ?: 0
                    val isLethal = dmg >= 21

                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isLethal) Color.Red else Color.White.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opponent.name,
                                color = opponent.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { onAddDamage(opponent.id, -1) },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("-", fontSize = 18.sp, color = Color.White)
                                }

                                Text(
                                    text = "$dmg/21",
                                    color = if (isLethal) Color.Red else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                Button(
                                    onClick = { onAddDamage(opponent.id, 1) },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+", fontSize = 18.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "Tip: Si recibes 21 de daño de un solo comandante, pierdes la partida.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
