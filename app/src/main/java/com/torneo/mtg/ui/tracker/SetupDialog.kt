package com.torneo.mtg.ui.tracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.torneo.mtg.domain.model.MtgFormat

@Composable
fun SetupDialog(
    onConfirm: (players: Int, format: MtgFormat) -> Unit
) {
    var selectedPlayers by remember { mutableStateOf(4) }
    var selectedFormat by remember { mutableStateOf(MtgFormat.COMMANDER) }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Configurar Partida MTG") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Número de Jugadores:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(2, 3, 4).forEach { count ->
                        FilterChip(
                            selected = selectedPlayers == count,
                            onClick = { selectedPlayers = count },
                            label = { Text("$count Jugadores") }
                        )
                    }
                }

                Text("Formato de Juego:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MtgFormat.entries.forEach { format ->
                        FilterChip(
                            selected = selectedFormat == format,
                            onClick = { selectedFormat = format },
                            label = { Text("${format.displayName} (${format.startingLife}V)") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedPlayers, selectedFormat) }) {
                Text("Iniciar Partida")
            }
        }
    )
}
