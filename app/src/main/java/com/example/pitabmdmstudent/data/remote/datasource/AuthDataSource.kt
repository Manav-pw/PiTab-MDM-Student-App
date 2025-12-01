package com.example.pitabmdmstudent.data.remote.datasource

import com.example.pitabmdmstudent.data.remote.network.ApiResponse
import com.example.pitabmdmstudent.models.auth.GetOtpRequestBody
import com.example.pitabmdmstudent.models.auth.GetTokenDto
import com.example.pitabmdmstudent.models.auth.GetTokenRequestBody
import com.example.pitabmdmstudent.models.auth.RegisterUserDto
import com.example.pitabmdmstudent.models.auth.RegisterUserRequestBody
import com.example.pitabmdmstudent.models.auth.ResendOtpRequestBody
import retrofit2.Response

interface AuthDataSource {

    suspend fun getOtp(body: GetOtpRequestBody): Response<ApiResponse<Unit>>

    suspend fun getToken(body: GetTokenRequestBody): Response<ApiResponse<GetTokenDto>>

    suspend fun refreshToken(body: Map<String, String>): Response<ApiResponse<GetTokenDto>>

    suspend fun resendOtp(body: ResendOtpRequestBody): Response<ApiResponse<Unit>>

    suspend fun registerUser(
        organizationId: String,
        body: RegisterUserRequestBody,
    ): Response<ApiResponse<RegisterUserDto>>
}


