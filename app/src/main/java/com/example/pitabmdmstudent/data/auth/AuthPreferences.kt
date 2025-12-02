package com.example.pitabmdmstudent.data.auth

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRES_AT = "access_token_expires_at"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_FIRST_NAME = "user_first_name"

        // For socket connection compatibility
        private const val KEY_SOCKET_TOKEN = "socket_token"
    }

    fun saveToken(
        accessToken: String,
        refreshToken: String,
        expiresInSeconds: Long,
        tokenId: String,
        userId: String,
        firstName: String,
    ) {
        val expiresAtMillis = System.currentTimeMillis() + expiresInSeconds * 1000
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putLong(KEY_EXPIRES_AT, expiresAtMillis)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_FIRST_NAME, firstName)
            .putString(KEY_SOCKET_TOKEN, tokenId)
            .apply()
    }

    fun getAccessToken(): String? {
        val token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        val expiresAt = sharedPreferences.getLong(KEY_EXPIRES_AT, 0L)
        return if (!token.isNullOrEmpty() && expiresAt > System.currentTimeMillis()) {
            token
        } else {
            null
        }
    }

    fun getRefreshToken(): String? =
        sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun hasValidAccessToken(): Boolean = getAccessToken() != null

    fun saveDeviceLogin(
        socketToken: String,
        userId: String,
        userName: String?,
    ) {
        sharedPreferences.edit()
            .putString(KEY_SOCKET_TOKEN, socketToken)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_FIRST_NAME, userName ?: "")
            .apply()
    }

    fun clear() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_EXPIRES_AT)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_FIRST_NAME)
            .remove(KEY_SOCKET_TOKEN)
            .apply()
    }
}


