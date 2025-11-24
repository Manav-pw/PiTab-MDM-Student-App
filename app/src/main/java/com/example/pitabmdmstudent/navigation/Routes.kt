package com.example.pitabmdmstudent.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash_screen")
    object Permission: Routes("permission_screen")
    object Dashboard : Routes("dashboard_screen")
    object ScanQR : Routes("scan_qr_screen/{pairing_code}")
}