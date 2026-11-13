package com.torneo.mtg.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.torneo.mtg.ui.admin.AdminDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    playerName: String,
    onNavigateToTracker: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Portal del Jugador", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(playerName, fontSize = 12.sp, color = Color(0xFF818CF8))
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToTracker) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Life Tracker", tint = Color(0xFFFB7185))
                    }
                    IconButton(onClick = { viewModel.loadTournaments() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color(0xFFF87171))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0F172A))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE11D48)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Partida en Mesa - MTG Life Tracker", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Abre el contador de vidas (2 a 4 jugadores) con veneno y comandante.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToTracker,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Abrir Life Tracker")
                        }
                    }
                }
            }

            item {
                Text("Encuentros y Estado del Torneo", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }

            if (state.matches.isEmpty()) {
                item {
                    Text("No hay encuentros activos todavía. El administrador generará los emparejamientos en breve.", color = Color(0xFF64748B), fontSize = 12.sp)
                }
            } else {
                items(state.matches) { m ->
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Mesa ${m.tableNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF818CF8), fontSize = 13.sp)
                                Text(m.players.joinToString(" vs ") { it.name }, color = Color.White, fontSize = 12.sp)
                            }
                            Text(
                                text = if (m.winnerName != null) "Ganador: ${m.winnerName}" else "En disputa",
                                color = if (m.winnerName != null) Color(0xFF34D399) else Color(0xFFFBBF24),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
