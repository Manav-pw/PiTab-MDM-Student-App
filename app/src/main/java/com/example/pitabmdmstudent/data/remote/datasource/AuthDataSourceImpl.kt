package com.example.pitabmdmstudent.data.remote.datasource

import com.example.pitabmdmstudent.data.remote.api.AuthApi
import com.example.pitabmdmstudent.data.remote.network.ApiResponse
import com.example.pitabmdmstudent.models.auth.GetOtpRequestBody
import com.example.pitabmdmstudent.models.auth.GetTokenDto
import com.example.pitabmdmstudent.models.auth.GetTokenRequestBody
import com.example.pitabmdmstudent.models.auth.RegisterUserDto
import com.example.pitabmdmstudent.models.auth.RegisterUserRequestBody
import com.example.pitabmdmstudent.models.auth.ResendOtpRequestBody
import retrofit2.Response
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val api: AuthApi,
) : AuthDataSource {

    override suspend fun getOtp(body: GetOtpRequestBody): Response<ApiResponse<Unit>> {
        return api.getOtp(body)
    }

    override suspend fun getToken(body: GetTokenRequestBody): Response<ApiResponse<GetTokenDto>> {
        return api.getToken(body)
    }

    override suspend fun refreshToken(body: Map<String, String>): Response<ApiResponse<GetTokenDto>> {
        return api.refreshToken(body)
    }

    override suspend fun resendOtp(body: ResendOtpRequestBody): Response<ApiResponse<Unit>> {
        return api.resendOtp(body)
    }

    override suspend fun registerUser(
        organizationId: String,
        body: RegisterUserRequestBody,
    ): Response<ApiResponse<RegisterUserDto>> {
        return api.registerUser(organizationId, body)
    }
}


