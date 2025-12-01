package com.example.pitabmdmstudent.data.remote.api

import com.example.pitabmdmstudent.data.remote.network.ApiResponse
import com.example.pitabmdmstudent.models.auth.GetOtpRequestBody
import com.example.pitabmdmstudent.models.auth.GetTokenDto
import com.example.pitabmdmstudent.models.auth.GetTokenRequestBody
import com.example.pitabmdmstudent.models.auth.RegisterUserDto
import com.example.pitabmdmstudent.models.auth.RegisterUserRequestBody
import com.example.pitabmdmstudent.models.auth.ResendOtpRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {

    @POST("v3/users/get-otp")
    suspend fun getOtp(
        @Body body: GetOtpRequestBody,
    ): Response<ApiResponse<Unit>>

    @POST("v3/oauth/token")
    suspend fun getToken(
        @Body body: GetTokenRequestBody,
    ): Response<ApiResponse<GetTokenDto>>

    @POST("v3/oauth/refresh-token")
    suspend fun refreshToken(
        @Body body: Map<String, String>,
    ): Response<ApiResponse<GetTokenDto>>

    @POST("v3/users/resend-otp")
    suspend fun resendOtp(
        @Body body: ResendOtpRequestBody,
    ): Response<ApiResponse<Unit>>

    @POST("v3/users/register/{organizationId}")
    suspend fun registerUser(
        @Path("organizationId") organizationId: String,
        @Body body: RegisterUserRequestBody,
    ): Response<ApiResponse<RegisterUserDto>>
}


