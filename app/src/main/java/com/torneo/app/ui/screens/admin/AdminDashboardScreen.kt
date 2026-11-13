package com.torneo.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.torneo.app.data.repository.AuthRepository
import com.torneo.app.data.repository.TournamentRepository
import com.torneo.app.domain.model.Tournament
import com.torneo.app.domain.model.User
import com.torneo.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    user: User,
    authRepository: AuthRepository,
    tournamentRepository: TournamentRepository,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var tournaments by remember { mutableStateOf<List<Tournament>>(emptyList()) }
    var selectedTournament by remember { mutableStateOf<Tournament?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val res = tournamentRepository.getTournaments()
            res.onSuccess {
                tournaments = it
                if (it.isNotEmpty()) {
                    selectedTournament = it.first()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = selectedTournament?.name ?: "Torneo Master",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Admin: ${user.name} (${user.email})",
                            fontSize = 11.sp,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authRepository.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Slate700)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                    label = { Text("1. Inscribir", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        indicatorColor = IndigoLight
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Shuffle, contentDescription = null) },
                    label = { Text("2. Emparejar", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        indicatorColor = IndigoLight
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                    label = { Text("3. Resultados", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        indicatorColor = IndigoLight
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val tId = selectedTournament?.id ?: "t-default-01"
            when (selectedTab) {
                0 -> TabInscribirScreen(tournamentId = tId, repository = tournamentRepository)
                1 -> TabEmparejarScreen(
                    tournamentId = tId,
                    repository = tournamentRepository,
                    onNavigateToResults = { selectedTab = 2 }
                )
                2 -> TabResultadosScreen(tournamentId = tId, repository = tournamentRepository, isAdmin = true)
            }
        }
    }
}
