package com.torneo.app.data.repository

import android.content.Context
import com.torneo.app.data.api.ApiClient
import com.torneo.app.data.dto.GoogleLoginRequestDto
import com.torneo.app.data.dto.LoginRequestDto
import com.torneo.app.data.dto.RegisterRequestDto
import com.torneo.app.domain.model.Role
import com.torneo.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {

    private val api = ApiClient.getService(context)
    private val sessionManager = SessionManager(context)

    suspend fun login(email: String, password: String):Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequestDto(email, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val user = User(
                    id = body.user.id,
                    name = body.user.name,
                    email = body.user.email,
                    role = if (body.user.role.equals("ADMIN", ignoreCase = true)) Role.ADMIN else Role.CLIENT,
                    avatarUrl = body.user.avatarUrl
                )
                sessionManager.saveSession(body.token, user)
                Result.success(user)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Credenciales incorrectas"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String, role: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(RegisterRequestDto(name, email, password, role))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val user = User(
                    id = body.user.id,
                    name = body.user.name,
                    email = body.user.email,
                    role = if (body.user.role.equals("ADMIN", ignoreCase = true)) Role.ADMIN else Role.CLIENT,
                    avatarUrl = body.user.avatarUrl
                )
                sessionManager.saveSession(body.token, user)
                Result.success(user)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al registrar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.googleLogin(GoogleLoginRequestDto(idToken))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val user = User(
                    id = body.user.id,
                    name = body.user.name,
                    email = body.user.email,
                    role = if (body.user.role.equals("ADMIN", ignoreCase = true)) Role.ADMIN else Role.CLIENT,
                    avatarUrl = body.user.avatarUrl
                )
                sessionManager.saveSession(body.token, user)
                Result.success(user)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error en autenticación con Google"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getLoggedUser(): User? = sessionManager.getUser()
    fun logout() = sessionManager.clearSession()
}
