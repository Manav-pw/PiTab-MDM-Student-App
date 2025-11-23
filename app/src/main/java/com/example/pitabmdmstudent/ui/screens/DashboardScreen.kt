package com.example.pitabmdmstudent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pitabmdmstudent.models.AppUsage
import com.example.pitabmdmstudent.ui.components.AllAppsCard
import com.example.pitabmdmstudent.ui.components.DailyAverageCard
import com.example.pitabmdmstudent.ui.components.MostUsedAppCard
import com.example.pitabmdmstudent.ui.components.TopBar

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.padding(25.dp))
        TopBar()
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
                val list : List<AppUsage> = listOf(AppUsage("Google",2),AppUsage("App 2",5), AppUsage("Meta",30))
                AllAppsCard(list)
            }
        }
    }
}