package com.example.pitabmdmstudent.models.auth

data class UserData(
    val id: String,
    val firstName: String,
)

data class GetTokenDto(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Long,
    val tokenId: String,
    val user: UserData,
)

data class GetOtpRequestBody(
    val username: String,
    val countryCode: String,
    val organizationId: String?,
)

data class GetTokenRequestBody(
    val username: String,
    val otp: String,
    val client_id: String = "system-admin",
    val grant_type: String = "password",
    val organizationId: String?,
)

data class ResendOtpRequestBody(
    val mobile: String,
    val organizationId: String,
)

data class RegisterUserRequestBody(
    val firstName: String,
    val mobile: String,
    val countryCode: String,
)

data class RegisterUserDto(
    val _id: String,
    val username: String,
    val countryCode: String,
    val primaryNumber: String,
)


