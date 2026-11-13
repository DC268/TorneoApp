package com.torneo.mtg.ui.tracker

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CenterTimerHub(
    modifier: Modifier = Modifier,
    timerSeconds: Int,
    isRunning: Boolean,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = modifier
            .border(2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF151515).copy(alpha = 0.96f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeFormatted,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = if (timerSeconds < 300) Color(0xFFFF5252) else Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(onClick = onToggleTimer, modifier = Modifier.size(30.dp)) {
                    Text(if (isRunning) "⏸" else "▶", color = Color.White, fontSize = 14.sp)
                }

                IconButton(onClick = onResetTimer, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Timer", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }

                IconButton(onClick = onOpenSettings, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
