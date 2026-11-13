package com.torneo.app.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.torneo.app.data.repository.AuthRepository
import com.torneo.app.data.repository.TournamentRepository
import com.torneo.app.domain.model.Tournament
import com.torneo.app.domain.model.User
import com.torneo.app.ui.screens.admin.TabResultadosScreen
import com.torneo.app.ui.theme.IndigoPrimary
import com.torneo.app.ui.theme.Slate700
import com.torneo.app.ui.theme.Slate900
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    user: User,
    authRepository: AuthRepository,
    tournamentRepository: TournamentRepository,
    onLogout: () -> Unit
) {
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
                            text = selectedTournament?.name ?: "Torneo en Vivo",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Cliente: ${user.name} • Vista Espectador",
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val tId = selectedTournament?.id ?: "t-default-01"
            TabResultadosScreen(
                tournamentId = tId,
                repository = tournamentRepository,
                isAdmin = false
            )
        }
    }
}
