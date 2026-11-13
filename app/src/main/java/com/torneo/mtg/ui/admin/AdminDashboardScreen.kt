package com.torneo.mtg.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.torneo.mtg.data.model.MatchResponse
import com.torneo.mtg.data.model.ParticipantResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminName: String,
    onNavigateToTracker: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("1. Inscribir", "2. Emparejar", "3. Resultados")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Administrador", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(adminName, fontSize = 12.sp, color = Color(0xFF818CF8))
                    }
                },
                actions = {
                    // Open MTG Life Tracker
                    IconButton(onClick = onNavigateToTracker) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Life Tracker", tint = Color(0xFFFB7185))
                    }
                    // Refresh
                    IconButton(onClick = { viewModel.loadTournaments() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                    }
                    // Logout
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color(0xFFF87171))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1E293B)) {
                tabTitles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.PersonAdd, contentDescription = null)
                                1 -> Icon(Icons.Default.Shuffle, contentDescription = null)
                                else -> Icon(Icons.Default.EmojiEvents, contentDescription = null)
                            }
                        },
                        label = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF818CF8),
                            selectedTextColor = Color(0xFF818CF8),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8),
                            indicatorColor = Color(0xFF312E81)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0F172A))
        ) {
            when (selectedTabIndex) {
                0 -> TabInscribirView(state.participants, viewModel)
                1 -> TabEmparejarView(state.matches, viewModel)
                2 -> TabResultadosView(state.matches, viewModel)
            }
        }
    }
}

// ----------------- PESTAÑA 1: INSCRIBIR PERSONAS -----------------
@Composable
fun TabInscribirView(
    participants: List<ParticipantResponse>,
    viewModel: AdminDashboardViewModel
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var deck by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Inscribir Persona al Torneo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo (opcional)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deck,
                        onValueChange = { deck = it },
                        label = { Text("Mazo / Arquetipo MTG") },
                        placeholder = { Text("Ej: Izzet Murktide") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.registerParticipant(name, email, deck)
                                name = ""
                                email = ""
                                deck = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Inscribir Participante")
                    }
                }
            }
        }

        item {
            Text(
                "Participantes Inscritos (${participants.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )
        }

        items(participants) { p ->
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(p.deckName ?: "Sin mazo", color = Color(0xFF818CF8), fontSize = 12.sp)
                        Text("Puntos: ${p.matchPoints}", color = Color(0xFFFBBF24), fontSize = 11.sp)
                    }
                    IconButton(onClick = { viewModel.removeParticipant(p.userId) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFF87171))
                    }
                }
            }
        }
    }
}

// ----------------- PESTAÑA 2: EMPAREJAR RANDOM -----------------
@Composable
fun TabEmparejarView(
    matches: List<MatchResponse>,
    viewModel: AdminDashboardViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Emparejamiento Automático y Aleatorio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Genera mesas 1v1 al azar de forma justa entre los inscritos.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.generateRandomPairings() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Emparejar Aleatoriamente")
                    }
                }
            }
        }

        if (matches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no se han generado emparejamientos.", color = Color(0xFF64748B), fontSize = 13.sp)
                }
            }
        } else {
            items(matches) { m ->
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Mesa #${m.tableNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF818CF8), fontSize = 13.sp)
                            Text(m.status, color = if (m.status == "COMPLETED") Color(0xFF34D399) else Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        m.players.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(p.name, color = Color.White, fontSize = 13.sp)
                                if (m.status != "COMPLETED") {
                                    Button(
                                        onClick = { viewModel.submitMatchResult(m.id, p.userId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("Ganó", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        if (m.winnerName != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("🏆 Ganador: ${m.winnerName}", color = Color(0xFF34D399), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ----------------- PESTAÑA 3: VER RESULTADOS -----------------
@Composable
fun TabResultadosView(
    matches: List<MatchResponse>,
    viewModel: AdminDashboardViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Resultados de los Encuentros",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (matches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay resultados registrados todavía.", color = Color(0xFF64748B), fontSize = 13.sp)
                }
            }
        } else {
            items(matches) { m ->
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
                            Text("Mesa ${m.tableNumber} - Ronda ${m.roundNumber}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text(m.players.joinToString(" vs ") { it.name }, color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Text(
                            text = if (m.winnerName != null) "Ganó: ${m.winnerName}" else "En disputa",
                            color = if (m.winnerName != null) Color(0xFF34D399) else Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
