package com.example.pitabmdmstudent.ui.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pitabmdmstudent.models.AppUsage
import com.example.pitabmdmstudent.ui.components.AllAppsCard
import com.example.pitabmdmstudent.ui.components.DailyAverageCard
import com.example.pitabmdmstudent.ui.components.MostUsedAppCard
import com.example.pitabmdmstudent.ui.components.TopBar
import com.example.pitabmdmstudent.viewmodel.DashboardViewModel
import com.example.pitabmdmstudent.viewmodel.StudentViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.set
import com.example.pitabmdmstudent.navigation.Routes
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardVm: DashboardViewModel = hiltViewModel(),
    studentVm: StudentViewModel = hiltViewModel()
) {
    val usageList by dashboardVm.appUsageFlow.collectAsState()

    val code by studentVm.pairingCode.collectAsState()


    LaunchedEffect(Unit) {
        dashboardVm.loadWeeklyUsage()
    }

    LaunchedEffect(usageList) {
        Log.d("DashboardScreen", "Usage data: $usageList")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.padding(25.dp))
        TopBar(onConnectClick = {
            studentVm.loadPairingCode { generatedCode ->
                val encoded = URLEncoder.encode(generatedCode, StandardCharsets.UTF_8.toString())
                val route = Routes.ScanQR.route.replace("{pairing_code}", encoded)
                navController.navigate(route){
                    launchSingleTop = true
                }
            }
        })
        Row(
            modifier = Modifier.fillMaxSize().padding(top = 30.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                DailyAverageCard()
                MostUsedAppCard()
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val list : List<AppUsage> = listOf(AppUsage("App 1",2),AppUsage("App 2",5), AppUsage("App 3",30))
                AllAppsCard(list)
            }
        }
    }
}