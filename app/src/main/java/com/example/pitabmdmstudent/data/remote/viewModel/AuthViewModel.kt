package com.example.pitabmdmstudent.data.remote.viewModel

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

    fun requestOtp(phoneNumber: String, countryCode: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, otpSent = false)
        viewModelScope.launch {
            val success = authRepository.requestOtp(phoneNumber, countryCode)
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
        onSuccess: () -> Unit,
        onRequireName: () -> Unit,
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val token = authRepository.getToken(phoneNumber, otp)
            if (token != null) {
                _uiState.value = _uiState.value.copy(isLoading = false, requireName = false)
                onSuccess()
            } else {
                // If token is null we assume user might not be registered yet.
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    requireName = true,
                    errorMessage = "User might not be registered. Please enter your name.",
                )
                onRequireName()
            }
        }
    }

    fun registerUser(
        firstName: String,
        phoneNumber: String,
        countryCode: String,
        otp: String,
        onSuccess: () -> Unit,
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val registered = authRepository.registerUser(firstName, phoneNumber, countryCode)
            if (registered != null) {
                // After successful registration, attempt to get token again
                val token = authRepository.getToken(phoneNumber, otp)
                if (token != null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, requireName = false)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Registration successful but login failed. Please try again.",
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Registration failed. Please check details and try again.",
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}


