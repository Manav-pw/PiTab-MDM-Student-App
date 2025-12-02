package com.example.pitabmdmstudent.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pitabmdmstudent.data.remote.viewModel.AuthViewModel
import com.example.pitabmdmstudent.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val countryCodeState = remember { mutableStateOf("+91") }
    val phoneState = remember { mutableStateOf("") }
    val otpState = remember { mutableStateOf("") }
    val nameState = remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = countryCodeState.value,
            onValueChange = { countryCodeState.value = it },
            label = { Text("Country Code") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneState.value,
            onValueChange = { phoneState.value = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.requestOtp(
                    phoneNumber = phoneState.value,
                    countryCode = countryCodeState.value,
                )
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Send OTP")
        }

        if (uiState.otpSent) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otpState.value,
                onValueChange = { otpState.value = it },
                label = { Text("OTP") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val androidId = android.provider.Settings.Secure.getString(
                        context.contentResolver,
                        android.provider.Settings.Secure.ANDROID_ID
                    ) ?: java.util.UUID.randomUUID().toString()

                    viewModel.verifyOtp(
                        phoneNumber = phoneState.value,
                        otp = otpState.value,
                        deviceOS = "android",
                        machineId = androidId,
                        onSuccess = {
                            navController.navigate(Routes.Splash.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        }
                    )
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Verify OTP")
            }
        }

        if (uiState.requireName) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.updateDeviceName(
                        name = nameState.value,
                    ) {
                        navController.navigate(Routes.Splash.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Register & Continue")
            }
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        uiState.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}


