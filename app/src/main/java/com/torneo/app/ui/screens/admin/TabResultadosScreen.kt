package com.torneo.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.torneo.app.data.repository.TournamentRepository
import com.torneo.app.domain.model.Match
import com.torneo.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TabResultadosScreen(
    tournamentId: String,
    repository: TournamentRepository,
    isAdmin: Boolean = true
) {
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var selectedMatchForEdit by remember { mutableStateOf<Match?>(null) }
    var scoreAInput by remember { mutableStateOf("") }
    var scoreBInput by remember { mutableStateOf("") }
    var selectedWinnerId by remember { mutableStateOf<String?>(null) }
    var notesInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadMatches() {
        scope.launch {
            isLoading = true
            val res = repository.getMatches(tournamentId)
            isLoading = false
            res.onSuccess { matches = it }
        }
    }

    LaunchedEffect(tournamentId) {
        loadMatches()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate50)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberWarning)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pestaña 3: Resultados de los Encuentros",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Slate900
                            )
                        }
                        Text(
                            text = if (isAdmin) "Actualiza marcadores, declara ganadores y avanza rondas." else "Consulta los marcadores y avances de las llaves en vivo.",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }
            }

            if (matches.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No hay encuentros disponibles todavía.", color = Slate500, fontSize = 13.sp)
                        }
                    }
                }
            }

            items(matches) { m ->
                val isCompleted = m.status == "COMPLETED"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = m.roundName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = IndigoPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• #${m.matchNumber}",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCompleted) EmeraldSuccess.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isCompleted) "Completado" else "Pendiente",
                                    color = if (isCompleted) EmeraldSuccess else AmberWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Competitor 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (m.winner != null && m.winner.id == m.participantA?.id) EmeraldSuccess.copy(alpha = 0.12f) else Slate50,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = m.participantA?.name ?: "Por definir",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate900
                            )
                            Text(
                                text = if (m.scoreA != null) "${m.scoreA}" else "-",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Slate900
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Competitor 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (m.winner != null && m.winner.id == m.participantB?.id) EmeraldSuccess.copy(alpha = 0.12f) else Slate50,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = m.participantB?.name ?: "BYE (Pase directo)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (m.participantB != null) Slate900 else Slate500
                            )
                            Text(
                                text = if (m.scoreB != null) "${m.scoreB}" else "-",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Slate900
                            )
                        }

                        if (m.winner != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ganador: ${m.winner.name}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        }

                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    selectedMatchForEdit = m
                                    scoreAInput = m.scoreA?.toString() ?: ""
                                    scoreBInput = m.scoreB?.toString() ?: ""
                                    selectedWinnerId = m.winner?.id
                                    notesInput = m.notes ?: ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Registrar / Modificar Marcador", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Dialog for editing scores / winner
        if (selectedMatchForEdit != null) {
            val match = selectedMatchForEdit!!
            AlertDialog(
                onDismissRequest = { selectedMatchForEdit = null },
                title = { Text("Registrar Resultado", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "${match.roundName} • Encuentro #${match.matchNumber}",
                            fontSize = 12.sp,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = scoreAInput,
                            onValueChange = { scoreAInput = it },
                            label = { Text("Marcador ${match.participantA?.name ?: "A"}") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = scoreBInput,
                            onValueChange = { scoreBInput = it },
                            label = { Text("Marcador ${match.participantB?.name ?: "B"}") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text("Seleccionar Ganador:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (match.participantA != null) {
                                FilterChip(
                                    selected = selectedWinnerId == match.participantA.id,
                                    onClick = { selectedWinnerId = match.participantA.id },
                                    label = { Text(match.participantA.name) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (match.participantB != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                FilterChip(
                                    selected = selectedWinnerId == match.participantB.id,
                                    onClick = { selectedWinnerId = match.participantB.id },
                                    label = { Text(match.participantB.name) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Observaciones / Notas") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                val sA = scoreAInput.toIntOrNull()
                                val sB = scoreBInput.toIntOrNull()
                                repository.updateMatchResult(
                                    match.id,
                                    sA,
                                    sB,
                                    selectedWinnerId,
                                    notesInput
                                )
                                selectedMatchForEdit = null
                                loadMatches()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedMatchForEdit = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
