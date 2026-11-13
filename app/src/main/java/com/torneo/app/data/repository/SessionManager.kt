package com.torneo.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.torneo.app.domain.model.Role
import com.torneo.app.domain.model.User

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("torneo_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER = "user_json"
        private const val KEY_CURRENT_TOURNAMENT = "current_tournament_id"
    }

    fun saveSession(token: String, user: User) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER, gson.toJson(user))
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun isAdmin(): Boolean {
        return getUser()?.role == Role.ADMIN
    }

    fun saveCurrentTournamentId(id: String) {
        prefs.edit().putString(KEY_CURRENT_TOURNAMENT, id).apply()
    }

    fun getCurrentTournamentId(): String? {
        return prefs.getString(KEY_CURRENT_TOURNAMENT, null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
