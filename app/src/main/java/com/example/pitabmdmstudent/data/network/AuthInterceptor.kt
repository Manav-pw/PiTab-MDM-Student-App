package com.example.pitabmdmstudent.data.network

import com.example.pitabmdmstudent.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Properties

class AuthInterceptor : Interceptor {
    val token: String = BuildConfig.PARENT_BEARER_TOKEN
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}