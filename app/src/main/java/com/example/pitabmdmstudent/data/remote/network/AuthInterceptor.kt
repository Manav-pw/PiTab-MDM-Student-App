package com.example.pitabmdmstudent.data.remote.network

import com.example.pitabmdmstudent.data.auth.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = authPreferences.getAccessToken()

        val request = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
