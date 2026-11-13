package com.torneo.app.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.torneo.app.data.repository.AuthRepository
import com.torneo.app.data.repository.TournamentRepository
import com.torneo.app.domain.model.Role
import com.torneo.app.domain.model.User
import com.torneo.app.ui.screens.admin.AdminDashboardScreen
import com.torneo.app.ui.screens.auth.LoginScreen
import com.torneo.app.ui.screens.auth.RegisterScreen
import com.torneo.app.ui.screens.client.ClientDashboardScreen

@Composable
fun AppNavigation(
    authRepository: AuthRepository,
    tournamentRepository: TournamentRepository
) {
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf(authRepository.getLoggedUser()) }

    val startDestination = when {
        currentUser?.role == Role.ADMIN -> "admin_dashboard"
        currentUser?.role == Role.CLIENT -> "client_dashboard"
        else -> "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = { user ->
                    currentUser = user
                    if (user.role == Role.ADMIN) {
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
                }
            )
        }

        composable("register") {
            RegisterScreen(
                authRepository = authRepository,
                onRegisterSuccess = { user ->
                    currentUser = user
                    if (user.role == Role.ADMIN) {
                        navController.navigate("admin_dashboard") {
                            popUpTo("register") { inclusive = true }
                        }
                    } else {
                        navController.navigate("client_dashboard") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("admin_dashboard") {
            currentUser?.let { user ->
                AdminDashboardScreen(
                    user = user,
                    authRepository = authRepository,
                    tournamentRepository = tournamentRepository,
                    onLogout = {
                        currentUser = null
                        navController.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("client_dashboard") {
            currentUser?.let { user ->
                ClientDashboardScreen(
                    user = user,
                    authRepository = authRepository,
                    tournamentRepository = tournamentRepository,
                    onLogout = {
                        currentUser = null
                        navController.navigate("login") {
                            popUpTo("client_dashboard") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
