package com.torneo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.torneo.app.data.repository.AuthRepository
import com.torneo.app.data.repository.TournamentRepository
import com.torneo.app.ui.navigation.AppNavigation
import com.torneo.app.ui.theme.TorneoAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository(applicationContext)
        val tournamentRepository = TournamentRepository(applicationContext)

        setContent {
            TorneoAppTheme {
                AppNavigation(
                    authRepository = authRepository,
                    tournamentRepository = tournamentRepository
                )
            }
        }
    }
}
