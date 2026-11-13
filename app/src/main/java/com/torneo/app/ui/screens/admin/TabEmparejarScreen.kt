package com.torneo.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Shuffle
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
fun TabEmparejarScreen(
    tournamentId: String,
    repository: TournamentRepository,
    onNavigateToResults: () -> Unit
) {
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IndigoPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Casino, contentDescription = null, tint = AmberWarning)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pestaña 2: Emparejamiento Automático y Random",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "El algoritmo distribuye a todos los inscritos en llaves de 1v1 de forma imparcial y aleatoria. Maneja pases directos (BYE) si la cantidad es impar.",
                        fontSize = 12.sp,
                        color = IndigoLight,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                message = null
                                val res = repository.generateRandomMatchmaking(tournamentId)
                                isLoading = false
                                res.fold(
                                    onSuccess = {
                                        matches = it
                                        message = "¡Emparejamiento aleatorio generado! ${it.size} cruces creados."
                                    },
                                    onFailure = { message = it.message }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, tint = IndigoPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Emparejar de Forma Random!",
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (message != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = message!!,
                            color = AmberWarning,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cruces y Llaves Generadas (${matches.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                if (matches.isNotEmpty()) {
                    TextButton(onClick = onNavigateToResults) {
                        Text("Ver Resultados >", color = IndigoPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Shuffle, contentDescription = null, tint = Slate500, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aún no se han generado emparejamientos.", color = Slate500, fontSize = 13.sp)
                            Text("Presiona el botón de arriba para sortear.", color = Slate500, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        items(matches) { m ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = m.roundName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                        Text(
                            text = "Encuentro #${m.matchNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate500
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Player A
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (m.winner != null && m.winner.id == m.participantA?.id) EmeraldSuccess.copy(alpha = 0.15f) else Slate100,
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
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Player B
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (m.winner != null && m.winner.id == m.participantB?.id) EmeraldSuccess.copy(alpha = 0.15f) else Slate100,
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
                            fontSize = 14.sp
                        )
                    }

                    if (!m.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = m.notes,
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }
            }
        }
    }
}
