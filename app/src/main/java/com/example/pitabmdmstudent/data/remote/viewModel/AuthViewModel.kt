package com.example.pitabmdmstudent.data.remote.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.data.remote.repository.AuthRepository
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

    fun hasValidAccessToken(): Boolean = authRepository.hasValidAccessToken()

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.clearSession()
        onLoggedOut()
    }

    fun requestOtp(phoneNumber: String, countryCode: String) {
        Log.d("OTP","request")
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
            val token = authRepository.getToken(phoneNumber, otp)
            if (token == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Login failed. Please check OTP and try again.",
                )
                return@launch
            }

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


