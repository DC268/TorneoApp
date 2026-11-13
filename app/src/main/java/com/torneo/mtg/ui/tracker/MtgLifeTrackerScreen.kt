package com.torneo.mtg.ui.tracker

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.torneo.mtg.domain.model.PlayerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MtgLifeTrackerScreen(
    onNavigateBack: () -> Unit,
    viewModel: MtgLifeTrackerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // WAKELOCK: Mantiene la pantalla encendida permanentemente mientras se juega
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Modal de selección de 2, 3 o 4 jugadores y formato (NO se salta)
    if (!state.isSetupFinished) {
        SetupDialog(
            initialPlayers = state.playerCount,
            initialFormat = state.format,
            onConfirm = { players, format ->
                viewModel.initializeGame(players, format, 20)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MTG Life Tracker", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF101010))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            when (state.playerCount) {
                2 -> TwoPlayersLayout(state.players, viewModel)
                3 -> ThreePlayersLayout(state.players, viewModel)
                else -> FourPlayersLayout(state.players, viewModel)
            }

            // Relojes en la mitad de la pantalla con el color de cada jugador y controles
            CenterTimerHub(
                modifier = Modifier.align(Alignment.Center),
                players = state.players,
                isAnyRunning = state.isAnyTimerRunning,
                onTogglePlayerTimer = { playerId -> viewModel.togglePlayerTimer(playerId) },
                onToggleGlobalTimer = { viewModel.toggleGlobalTimer() },
                onPassTurn = { viewModel.passTurn() },
                onResetMatch = { viewModel.resetMatch() },
                onOpenSettings = { viewModel.openSetup() }
            )
        }
    }
}

// -----------------------------------------------------------------
// LAYOUT PARA 2 JUGADORES (División 50/50 - Jugador 2 invertido 180°)
// -----------------------------------------------------------------
@Composable
fun TwoPlayersLayout(players: List<PlayerState>, viewModel: MtgLifeTrackerViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (players.size >= 2) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .rotate(180f)
            ) {
                PlayerZone(player = players[1], allPlayers = players, viewModel = viewModel)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                PlayerZone(player = players[0], allPlayers = players, viewModel = viewModel)
            }
        }
    }
}

// -----------------------------------------------------------------
// LAYOUT PARA 3 JUGADORES (Arriba 2 jugadores invertidos, abajo 1 centrado)
// -----------------------------------------------------------------
@Composable
fun ThreePlayersLayout(players: List<PlayerState>, viewModel: MtgLifeTrackerViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (players.size >= 3) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().rotate(180f)) {
                    PlayerZone(player = players[1], allPlayers = players, viewModel = viewModel)
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight().rotate(180f)) {
                    PlayerZone(player = players[2], allPlayers = players, viewModel = viewModel)
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                PlayerZone(player = players[0], allPlayers = players, viewModel = viewModel)
            }
        }
    }
}

// -----------------------------------------------------------------
// LAYOUT PARA 4 JUGADORES (Cuadrícula 2x2 - Fila superior invertida)
// -----------------------------------------------------------------
@Composable
fun FourPlayersLayout(players: List<PlayerState>, viewModel: MtgLifeTrackerViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (players.size >= 4) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().rotate(180f)) {
                    PlayerZone(player = players[2], allPlayers = players, viewModel = viewModel)
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight().rotate(180f)) {
                    PlayerZone(player = players[3], allPlayers = players, viewModel = viewModel)
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    PlayerZone(player = players[0], allPlayers = players, viewModel = viewModel)
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    PlayerZone(player = players[1], allPlayers = players, viewModel = viewModel)
                }
            }
        }
    }
}
