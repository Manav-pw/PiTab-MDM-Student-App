package com.example.pitabmdmstudent.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash_screen")
    object Home : Routes("home_screen")
}