package com.torneo.mtg.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.torneo.mtg.data.auth.SessionManager
import com.torneo.mtg.data.model.GoogleAuthRequest
import com.torneo.mtg.data.model.LoginRequest
import com.torneo.mtg.data.model.RegisterRequest
import com.torneo.mtg.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val role: String, val name: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.login(LoginRequest(email.trim(), pass))
                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!
                    sessionManager.saveSession(body.token, body.id, body.name, body.email, body.role)
                    _uiState.value = AuthUiState.Success(body.role, body.name)
                } else {
                    _uiState.value = AuthUiState.Error("Error: ${res.code()} - Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Error de conexión con el servidor")
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        val passwordRegex = "^(?=.*[a-zA-Z])(?=.*[0-9]).{6,}$".toRegex()
        if (!passwordRegex.matches(pass)) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener letras y números (mínimo 6 caracteres)")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.register(RegisterRequest(name.trim(), email.trim(), pass))
                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!
                    sessionManager.saveSession(body.token, body.id, body.name, body.email, body.role)
                    _uiState.value = AuthUiState.Success(body.role, body.name)
                } else {
                    _uiState.value = AuthUiState.Error("Error al registrarse: ${res.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    fun loginWithGoogle(email: String = "google.user@torneo.local", name: String = "Usuario Google") {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val api = ApiClient.getService(getApplication())
                val res = api.googleAuth(
                    GoogleAuthRequest(
                        idToken = "android-google-oauth-token",
                        email = email,
                        name = name
                    )
                )
                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!
                    sessionManager.saveSession(body.token, body.id, body.name, body.email, body.role)
                    _uiState.value = AuthUiState.Success(body.role, body.name)
                } else {
                    _uiState.value = AuthUiState.Error("Error en Google Sign-In: ${res.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }

    fun setServerIp(ip: String) {
        val formatted = if (ip.startsWith("http://") || ip.startsWith("https://")) ip else "http://$ip:8080/"
        sessionManager.setServerUrl(formatted)
        ApiClient.invalidateCache()
    }

    fun getServerIp(): String = sessionManager.getServerUrl()
}
