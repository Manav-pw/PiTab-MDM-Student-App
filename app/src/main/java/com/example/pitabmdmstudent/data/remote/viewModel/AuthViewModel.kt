package com.example.pitabmdmstudent.data.remote.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.data.remote.repository.AuthRepository
import com.example.pitabmdmstudent.models.auth.DeviceLoginRes
import com.example.pitabmdmstudent.models.auth.GetTokenDto
import com.example.pitabmdmstudent.models.auth.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val otpSent: Boolean = false,
    val requireName: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val DEV_PHONE = "9999999999"

    fun hasValidAccessToken(): Boolean = authRepository.hasValidAccessToken()

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.clearSession()
        onLoggedOut()
    }

    private fun runDevBypassLogic(onSuccess: () -> Unit) {
        Log.d("DevLogin","1")
        val devToken = GetTokenDto(
            access_token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjUzODMyMzMuMTUsImRhdGEiOnsiX2lkIjoiNjkxNWNmYzU1MDc4MTcwMTZjN2JkYTU2IiwidXNlcm5hbWUiOiI5NTI4OTAzNDMxIiwiZmlyc3ROYW1lIjoiIiwibGFzdE5hbWUiOiIiLCJvcmdhbml6YXRpb24iOnsiX2lkIjoiNjU5M2I0YTllNjc4MjgwMDE4NzQyYzRjIiwid2Vic2l0ZSI6ImxlYXJub3MubGl2ZSIsIm5hbWUiOiJsZWFybi1vcyJ9LCJyb2xlcyI6WyI1YjI3YmQ5NjU4NDJmOTUwYTc3OGM2ZWYiXSwiY291bnRyeUdyb3VwIjoiSU4iLCJvbmVSb2xlcyI6W10sInR5cGUiOiJVU0VSIn0sImlhdCI6MTc2NDc3ODQzM30.5FaN7zTBbxhZUHZrwWs8LZv90VI-BbF0MWiZpWx7FmY",
            refresh_token="8e5a2547b0aa818a9f150328950d72c33630edf9c53163d4cd86bae6b3ee948d",
            expires_in=1765383233150,
            tokenId="693061c10d02b1a7201af624",
            user= UserData(
                id="6915cfc5507817016c7bda56",
                firstName="Param"
            )
        )
        Log.d("DevLogin","2")

        viewModelScope.launch {

            Log.d("DevLogin","3")
            authRepository.saveToken(devToken)

            Log.d("DevLogin","4")

            var deviceLogin: DeviceLoginRes? = null

            try{
                deviceLogin = authRepository.loginDevice(
                    phoneNumber = "9528903431",
                    deviceOS = "android",
                    machineId = "9528903431",
                )
            }catch (e: Exception){
                Log.d("DevLogin","4.1 $e")
            }

            Log.d("DevLogin","5 $deviceLogin")

            if (deviceLogin == null) {
                Log.d("DevLogin","6 $deviceLogin")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Device login failed. Please try again.",
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                requireName = false
            )

            onSuccess()
        }
    }

    fun requestOtp(phoneNumber: String, countryCode: String, onSuccessDev: () -> Unit) {

        // 🔥 Developer Backdoor: Skip OTP request API
        if (phoneNumber == DEV_PHONE) {
            Log.d("OTP", "DEV MODE: OTP skipped")

            runDevBypassLogic(onSuccessDev)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                otpSent = true,
                errorMessage = null
            )

            return
        }


        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, otpSent = false)
        viewModelScope.launch {
            val success = authRepository.requestOtp(phoneNumber, countryCode)
            Log.d("OTP","done $_uiState.value")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                otpSent = success,
                errorMessage = if (!success) "Failed to send OTP" else null,
            )
        }
    }

    fun verifyOtp(
        phoneNumber: String,
        otp: String,
        deviceOS: String,
        machineId: String,
        onSuccess: () -> Unit,
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {

            // Step 1: Verify OTP → Get token
            val token = authRepository.getToken(phoneNumber, otp)
            if (token == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Login failed. Please check OTP and try again.",
                )
                return@launch
            }

            // Step 2: Save token
            authRepository.saveToken(token)

            // Step 3: Device login
            val deviceLogin = authRepository.loginDevice(
                phoneNumber = phoneNumber,
                deviceOS = deviceOS,
                machineId = machineId,
            )

            if (deviceLogin == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Device login failed. Please try again.",
                )
                return@launch
            }

            // Step 4: Check if user has name
            val userName = deviceLogin.user?.name
            if (userName.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    requireName = true,
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    requireName = false,
                )

                onSuccess()
            }
        }
    }



    fun updateDeviceName(
        name: String,
        onSuccess: () -> Unit,
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val success = authRepository.updateDeviceName(name)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    requireName = false,
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to update name. Please try again.",
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}


