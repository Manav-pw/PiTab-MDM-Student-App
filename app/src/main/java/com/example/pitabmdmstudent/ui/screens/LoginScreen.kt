package com.example.pitabmdmstudent.ui.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pitabmdmstudent.R
import com.example.pitabmdmstudent.data.remote.viewModel.AuthViewModel
import com.example.pitabmdmstudent.data.remote.viewModel.AuthUiState
import com.example.pitabmdmstudent.navigation.Routes
import com.example.pitabmdmstudent.ui.components.CountryCode
import com.example.pitabmdmstudent.ui.components.CountryCodePickerBottomSheet
import kotlinx.coroutines.launch
import kotlin.random.Random

// Dark Theme Colors
private val DarkBackground = Color(0xFF161E26)
private val DarkSurface = Color(0xFF1E2832)
private val DarkSurfaceVariant = Color(0xFF2A3642)
private val PrimaryAccent = Color(0xFF3388D7)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFB0BEC5)
private val TextMuted = Color(0xFF78909C)
private val ErrorColor = Color(0xFFEF5350)
private val ShapeColor = Color(0xFF3388D7)

// Data class for shape items in the background
private data class ShapeItem(
    val drawableRes: Int,
    val rotation: Float
)

// Decorative background with scattered shapes in a grid
@Composable
private fun DecorativeBackground(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    val shapeResources = listOf(
        R.drawable.shape_donut,
        R.drawable.shape_s_curve,
        R.drawable.shape_pinwheel,
        R.drawable.shape_flower,
        R.drawable.shape_star,
        R.drawable.shape_diamond,
        R.drawable.shape_corner
    )
    
    val columns = if (isLandscape) 4 else 5
    val rows = if (isLandscape) 4 else 6
    
    val shapes = remember(isLandscape) {
        val random = Random(42)
        buildList {
            repeat(rows * columns) {
                add(
                    ShapeItem(
                        drawableRes = shapeResources[random.nextInt(shapeResources.size)],
                        rotation = random.nextFloat() * 360f
                    )
                )
            }
        }
    }

    val shapeSize = if (isLandscape) 48.dp else 56.dp

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        val shape = shapes[index]
                        Image(
                            painter = painterResource(id = shape.drawableRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(shapeSize)
                                .rotate(shape.rotation)
                                .alpha(0.15f),
                            colorFilter = ColorFilter.tint(ShapeColor)
                        )
                    }
                }
            }
        }
    }
}

// Reusable form content composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginFormContent(
    uiState: AuthUiState,
    selectedCountry: CountryCode,
    phoneState: String,
    otpState: String,
    nameState: String,
    onPhoneChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onCountryPickerClick: () -> Unit,
    onSendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onUpdateName: () -> Unit
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedBorderColor = PrimaryAccent,
        unfocusedBorderColor = DarkSurfaceVariant,
        focusedLabelColor = PrimaryAccent,
        unfocusedLabelColor = TextMuted,
        cursorColor = PrimaryAccent,
        focusedPlaceholderColor = TextMuted,
        unfocusedPlaceholderColor = TextMuted
    )

    // Initial Login Form
    AnimatedVisibility(
        visible = !uiState.otpSent && !uiState.requireName,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Column {
            Text(
                text = "Welcome",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )

            Text(
                text = "Enter your phone number to continue",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextSecondary
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onCountryPickerClick,
                    modifier = Modifier.height(56.dp),
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCountry.code,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Country",
                            modifier = Modifier.size(20.dp),
                            tint = TextSecondary
                        )
                    }
                }

                OutlinedTextField(
                    value = phoneState,
                    onValueChange = { 
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            onPhoneChange(it)
                        }
                    },
                    placeholder = { 
                        Text(
                            "Phone Number",
                            color = TextMuted
                        ) 
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSendOtp,
                enabled = !uiState.isLoading && phoneState.length >= 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    contentColor = TextPrimary,
                    disabledContainerColor = PrimaryAccent.copy(alpha = 0.4f),
                    disabledContentColor = TextPrimary.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Send OTP",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = ErrorColor,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // OTP Verification Form
    AnimatedVisibility(
        visible = uiState.otpSent && !uiState.requireName,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Column {
            Text(
                text = "Verify OTP",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )

            Text(
                text = "Enter the OTP sent to ${selectedCountry.code} $phoneState",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextSecondary
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = otpState,
                onValueChange = { 
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        onOtpChange(it)
                    }
                },
                label = { Text("OTP", color = TextMuted) },
                placeholder = { Text("Enter 6-digit OTP", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onVerifyOtp,
                enabled = !uiState.isLoading && otpState.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    contentColor = TextPrimary,
                    disabledContainerColor = PrimaryAccent.copy(alpha = 0.4f),
                    disabledContentColor = TextPrimary.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Verify OTP",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = ErrorColor,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Name Registration Form
    AnimatedVisibility(
        visible = uiState.requireName,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Column {
            Text(
                text = "One Last Step",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )

            Text(
                text = "Enter your name to complete registration",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextSecondary
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = nameState,
                onValueChange = onNameChange,
                label = { Text("Name", color = TextMuted) },
                placeholder = { Text("Enter your name", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onUpdateName,
                enabled = !uiState.isLoading && nameState.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    contentColor = TextPrimary,
                    disabledContainerColor = PrimaryAccent.copy(alpha = 0.4f),
                    disabledContentColor = TextPrimary.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = ErrorColor,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedCountry by remember { mutableStateOf(CountryCode("India", "+91")) }
    var phoneState by remember { mutableStateOf("") }
    var otpState by remember { mutableStateOf("") }
    var nameState by remember { mutableStateOf("") }
    var showCountryPicker by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()
    
    // Set status bar to light icons (for dark background)
    val activity = context as? Activity
    activity?.let {
        val window = it.window
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (isLandscape) {
            // Landscape Layout - Side by side
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Section - Logo with background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    DecorativeBackground(
                        modifier = Modifier.fillMaxSize(),
                        isLandscape = true
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pifocus_logo),
                            contentDescription = "PiFocus Logo",
                            modifier = Modifier.size(100.dp),
                            colorFilter = ColorFilter.tint(PrimaryAccent)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "PiFocus",
                            style = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }

                // Right Section - Form
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    color = DarkSurface,
                    shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoginFormContent(
                            uiState = uiState,
                            selectedCountry = selectedCountry,
                            phoneState = phoneState,
                            otpState = otpState,
                            nameState = nameState,
                            onPhoneChange = { phoneState = it },
                            onOtpChange = { otpState = it },
                            onNameChange = { nameState = it },
                            onCountryPickerClick = { showCountryPicker = true },
                            onSendOtp = {
                                viewModel.requestOtp(
                                    phoneNumber = phoneState,
                                    countryCode = selectedCountry.code,
                                )
                            },
                            onVerifyOtp = {
                                val androidId = android.provider.Settings.Secure.getString(
                                    context.contentResolver,
                                    android.provider.Settings.Secure.ANDROID_ID
                                ) ?: java.util.UUID.randomUUID().toString()

                                viewModel.verifyOtp(
                                    phoneNumber = phoneState,
                                    otp = otpState,
                                    deviceOS = "android",
                                    machineId = androidId,
                                    onSuccess = {
                                        navController.navigate(Routes.Splash.route) {
                                            popUpTo(Routes.Login.route) { inclusive = true }
                                        }
                                    }
                                )
                            },
                            onUpdateName = {
                                viewModel.updateDeviceName(name = nameState) {
                                    navController.navigate(Routes.Splash.route) {
                                        popUpTo(Routes.Login.route) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        } else {
            // Portrait Layout - Top and bottom
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Section - 2/3 of the screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    contentAlignment = Alignment.Center
                ) {
                    DecorativeBackground(
                        modifier = Modifier.fillMaxSize(),
                        isLandscape = false
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pifocus_logo),
                            contentDescription = "PiFocus Logo",
                            modifier = Modifier.size(120.dp),
                            colorFilter = ColorFilter.tint(PrimaryAccent)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "PiFocus",
                            style = TextStyle(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }

                // Bottom Section - 1/3 of the screen
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = DarkSurface,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        LoginFormContent(
                            uiState = uiState,
                            selectedCountry = selectedCountry,
                            phoneState = phoneState,
                            otpState = otpState,
                            nameState = nameState,
                            onPhoneChange = { phoneState = it },
                            onOtpChange = { otpState = it },
                            onNameChange = { nameState = it },
                            onCountryPickerClick = { showCountryPicker = true },
                            onSendOtp = {
                                viewModel.requestOtp(
                                    phoneNumber = phoneState,
                                    countryCode = selectedCountry.code,
                                )
                            },
                            onVerifyOtp = {
                                val androidId = android.provider.Settings.Secure.getString(
                                    context.contentResolver,
                                    android.provider.Settings.Secure.ANDROID_ID
                                ) ?: java.util.UUID.randomUUID().toString()

                                viewModel.verifyOtp(
                                    phoneNumber = phoneState,
                                    otp = otpState,
                                    deviceOS = "android",
                                    machineId = androidId,
                                    onSuccess = {
                                        navController.navigate(Routes.Splash.route) {
                                            popUpTo(Routes.Login.route) { inclusive = true }
                                        }
                                    }
                                )
                            },
                            onUpdateName = {
                                viewModel.updateDeviceName(name = nameState) {
                                    navController.navigate(Routes.Splash.route) {
                                        popUpTo(Routes.Login.route) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Country Code Picker Bottom Sheet
        CountryCodePickerBottomSheet(
            isVisible = showCountryPicker,
            sheetState = sheetState,
            onDismiss = { showCountryPicker = false },
            onCountrySelected = { country ->
                selectedCountry = country
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showCountryPicker = false
                }
            }
        )
    }
}
