package com.example.pitabmdmstudent.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pitabmdmstudent.ui.screens.DashboardScreen
import com.example.pitabmdmstudent.ui.screens.PermissionScreen
import com.example.pitabmdmstudent.ui.screens.SplashScreen
import okhttp3.Route

@Composable
fun NavGraph(navController: NavHostController) {
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
        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
    }
}