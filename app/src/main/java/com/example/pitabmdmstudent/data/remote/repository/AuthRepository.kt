package com.example.pitabmdmstudent.data.remote.repository

import com.example.pitabmdmstudent.BuildConfig
import com.example.pitabmdmstudent.data.auth.AuthPreferences
import com.example.pitabmdmstudent.data.remote.datasource.AuthDataSource
import com.example.pitabmdmstudent.models.auth.GetTokenDto
import com.example.pitabmdmstudent.models.auth.GetTokenRequestBody
import com.example.pitabmdmstudent.models.auth.GetOtpRequestBody
import com.example.pitabmdmstudent.models.auth.RegisterUserDto
import com.example.pitabmdmstudent.models.auth.RegisterUserRequestBody
import com.example.pitabmdmstudent.models.auth.ResendOtpRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val authPreferences: AuthPreferences,
) {

    private val organizationId: String?
        get() = BuildConfig.MDM_ORG_ID.takeIf { it.isNotEmpty() }

    suspend fun requestOtp(phoneNumber: String, countryCode: String): Boolean {
        val body = GetOtpRequestBody(
            username = phoneNumber,
            countryCode = countryCode,
            organizationId = organizationId,
        )
        val response = authDataSource.getOtp(body)
        return response.isSuccessful && (response.body()?.success == true)
    }

    suspend fun resendOtp(mobile: String): Boolean {
        val orgId = organizationId ?: return false
        val body = ResendOtpRequestBody(
            mobile = mobile,
            organizationId = orgId,
        )
        val response = authDataSource.resendOtp(body)
        return response.isSuccessful && (response.body()?.success == true)
    }

    suspend fun getToken(phoneNumber: String, otp: String): GetTokenDto? {
        val body = GetTokenRequestBody(
            username = phoneNumber,
            otp = otp,
            organizationId = organizationId,
        )
        val response = authDataSource.getToken(body)
        if (response.isSuccessful) {
            val dto = response.body()?.data
            if (dto != null) {
                saveToken(dto)
            }
            return dto
        }
        return null
    }

    suspend fun registerUser(
        firstName: String,
        mobile: String,
        countryCode: String,
    ): RegisterUserDto? {
        val orgId = organizationId ?: return null
        val body = RegisterUserRequestBody(
            firstName = firstName,
            mobile = mobile,
            countryCode = countryCode,
        )
        val response = authDataSource.registerUser(orgId, body)
        return if (response.isSuccessful) {
            response.body()?.data
        } else {
            null
        }
    }

    suspend fun refreshToken(): Boolean {
        val refreshToken = authPreferences.getRefreshToken() ?: return false
        val body = mapOf(
            "client_id" to "system-admin",
            "client_secret" to BuildConfig.PW_CLIENT_SECRET,
            "refresh_token" to refreshToken,
        )
        val response = authDataSource.refreshToken(body)
        if (response.isSuccessful) {
            val dto = response.body()?.data
            if (dto != null) {
                saveToken(dto)
                return true
            }
        }
        return false
    }

    fun hasValidAccessToken(): Boolean = authPreferences.hasValidAccessToken()

    private fun saveToken(dto: GetTokenDto) {
        authPreferences.saveToken(
            accessToken = dto.access_token,
            refreshToken = dto.refresh_token,
            expiresInSeconds = dto.expires_in,
            tokenId = dto.tokenId,
            userId = dto.user.id,
            firstName = dto.user.firstName,
        )
    }
}


