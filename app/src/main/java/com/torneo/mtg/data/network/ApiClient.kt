package com.torneo.mtg.data.network

import android.content.Context
import com.torneo.mtg.data.auth.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var currentUrl: String? = null
    private var cachedService: ApiService? = null

    fun getService(context: Context): ApiService {
        val session = SessionManager(context)
        val url = session.getServerUrl()

        if (cachedService != null && currentUrl == url) {
            return cachedService!!
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                session.getToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        currentUrl = url
        cachedService = retrofit.create(ApiService::class.java)
        return cachedService!!
    }

    fun invalidateCache() {
        cachedService = null
        currentUrl = null
    }
}
