package com.example.pitabmdmstudent.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pitabmdmstudent.ui.screens.DashboardScreen
import com.example.pitabmdmstudent.ui.screens.LoginScreen
import com.example.pitabmdmstudent.ui.screens.PermissionScreen
import com.example.pitabmdmstudent.ui.screens.ScanQRScreen
import com.example.pitabmdmstudent.ui.screens.SplashScreen
import java.util.Base64

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        composable(Routes.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Routes.Permission.route) {
            PermissionScreen(navController = navController)
        }
        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Routes.ScanQR.route) {
            val code = it.arguments
                ?.getString("pairing_code")
                ?.let { String(Base64.getUrlDecoder().decode(it)) }
            ScanQRScreen(navController = navController, pairingCode = code)
        }
    }
}