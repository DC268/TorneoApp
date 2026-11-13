package com.torneo.mtg.ui.tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.torneo.mtg.domain.model.PlayerState

@Composable
fun PlayerZone(
    player: PlayerState,
    allPlayers: List<PlayerState>,
    viewModel: MtgLifeTrackerViewModel
) {
    var showCommanderDialog by remember { mutableStateOf(false) }

    val backgroundColor = if (player.isDead) Color(0xFF212121) else player.color.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
    ) {
        if (player.isDead) {
            Surface(
                color = Color.Red.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("DERROTADO", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text(player.deathReason ?: "", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = player.name,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.modifyLife(player.id, -1) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                    modifier = Modifier.size(60.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("-", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "${player.life}",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Button(
                    onClick = { viewModel.modifyLife(player.id, 1) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                    modifier = Modifier.size(60.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "-",
                            color = Color.White,
                            fontSize = 16.sp,
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .clickable { viewModel.modifyPoison(player.id, 1) }
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.clickable { showCommanderDialog = true }
                ) {
                    val maxDmg = player.commanderDamage.values.maxOrNull() ?: 0
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👑 Cmd: $maxDmg/21",
                            color = if (maxDmg >= 21) Color.Red else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showCommanderDialog) {
        CommanderDamageDialog(
            player = player,
            opponents = allPlayers.filter { it.id != player.id },
            onAddDamage = { attackerId, delta ->
                viewModel.addCommanderDamage(player.id, attackerId, delta)
            },
            onDismiss = { showCommanderDialog = false }
        )
    }
}

@Composable
fun CommanderDamageDialog(
    player: PlayerState,
    opponents: List<PlayerState>,
    onAddDamage: (attackerId: Int, delta: Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daño de Comandante recibido por ${player.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                opponents.forEach { opponent ->
                    val dmg = player.commanderDamage[opponent.id] ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(opponent.name, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { onAddDamage(opponent.id, -1) },
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("-") }
                            Text(" $dmg / 21 ", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 13.sp)
                            Button(
                                onClick = { onAddDamage(opponent.id, 1) },
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("+") }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Listo") }
        }
    )
}
