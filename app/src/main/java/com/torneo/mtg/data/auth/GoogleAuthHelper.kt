package com.torneo.mtg.data.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.torneo.mtg.R

object GoogleAuthHelper {

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val serverClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            ""
        }

        val gsoBuilder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()

        // Si existe un server_client_id configurado, solicitamos el idToken
        if (serverClientId.isNotBlank() && !serverClientId.contains("mtgapp.apps.googleusercontent.com")) {
            gsoBuilder.requestIdToken(serverClientId)
        }

        return GoogleSignIn.getClient(context, gsoBuilder.build())
    }

    fun parseSignInResult(data: Intent?): Result<GoogleSignInAccount> {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                Result.success(account)
            } else {
                Result.failure(Exception("Cuenta no disponible"))
            }
        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
