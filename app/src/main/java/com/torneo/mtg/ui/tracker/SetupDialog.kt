package com.torneo.mtg.ui.tracker

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
import com.torneo.mtg.domain.model.MtgFormat

@Composable
fun SetupDialog(
    initialPlayers: Int = 2,
    initialFormat: MtgFormat = MtgFormat.COMMANDER,
    onConfirm: (players: Int, format: MtgFormat) -> Unit
) {
    var selectedPlayers by remember { mutableIntStateOf(initialPlayers) }
    var selectedFormat by remember { mutableStateOf(initialFormat) }

    AlertDialog(
        onDismissRequest = {},
        containerColor = Color(0xFF1E293B),
        title = {
            Text(
                "⚔ Configurar Partida MTG",
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Selecciona el número de jugadores:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )

                // Botones grandes para 2, 3 o 4 jugadores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2, 3, 4).forEach { count ->
                        val isSelected = selectedPlayers == count
                        Button(
                            onClick = { selectedPlayers = count },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF6366F1) else Color(0xFF334155)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Text(
                                "$count",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    "Formato de juego:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MtgFormat.entries.forEach { format ->
                        val isSelected = selectedFormat == format
                        Button(
                            onClick = { selectedFormat = format },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF4F46E5) else Color(0xFF334155)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    format.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "${format.startingLife} Vidas",
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color(0xFFFDE047) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "⏱ Tiempo: 20 minutos por jugador",
                            color = Color(0xFF34D399),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "👉 Desliza a la derecha sobre las vidas para ver el Daño de Comandante.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedPlayers, selectedFormat) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "¡Iniciar Partida!",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    )
}
