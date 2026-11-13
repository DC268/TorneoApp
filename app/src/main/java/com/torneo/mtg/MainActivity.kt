package com.torneo.mtg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.torneo.mtg.data.auth.SessionManager
import com.torneo.mtg.ui.admin.AdminDashboardScreen
import com.torneo.mtg.ui.auth.AuthViewModel
import com.torneo.mtg.ui.auth.LoginScreen
import com.torneo.mtg.ui.auth.RegisterScreen
import com.torneo.mtg.ui.client.ClientDashboardScreen
import com.torneo.mtg.ui.tracker.MtgLifeTrackerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TorneoAppNavigation()
        }
    }
}

@Composable
fun TorneoAppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val context = androidx.compose.ui.platform.LocalContext.current

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    if ("ADMIN".equals(role, ignoreCase = true)) {
                        navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("client_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToTracker = {
                    navController.navigate("life_tracker")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { role ->
                    if ("ADMIN".equals(role, ignoreCase = true)) {
                        navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("client_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                adminName = "David Cagua (Admin)",
                onNavigateToTracker = {
                    navController.navigate("life_tracker")
                },
                onLogout = {
                    authViewModel.logout(context)
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("client_dashboard") {
            ClientDashboardScreen(
                playerName = "Jugador MTG",
                onNavigateToTracker = {
                    navController.navigate("life_tracker")
                },
                onLogout = {
                    authViewModel.logout(context)
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("life_tracker") {
            MtgLifeTrackerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
