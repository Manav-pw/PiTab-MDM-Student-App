package com.example.pitabmdmstudent.ui.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pitabmdmstudent.models.AppUsage
import com.example.pitabmdmstudent.navigation.Routes
import com.example.pitabmdmstudent.ui.components.AllAppsCard
import com.example.pitabmdmstudent.ui.components.DailyAverageBar
import com.example.pitabmdmstudent.ui.components.DailyAverageCard
import com.example.pitabmdmstudent.ui.components.MostUsedAppCard
import com.example.pitabmdmstudent.ui.components.TopBar
import com.example.pitabmdmstudent.data.viewmodel.DashboardViewModel
import com.example.pitabmdmstudent.data.remote.viewModel.StudentViewModel
import com.example.pitabmdmstudent.data.remote.viewModel.AuthViewModel
import java.util.Base64

private enum class RangeFilter { DAY, WEEK }

@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardVm: DashboardViewModel = hiltViewModel(),
    studentVm: StudentViewModel = hiltViewModel(),
    authVm: AuthViewModel = hiltViewModel(),
) {
    val todayUsage by dashboardVm.todayUsageFlow.collectAsState()
    val weeklyUsage by dashboardVm.weeklyUsageFlow.collectAsState()
    val totalToday by dashboardVm.totalTodayFlow.collectAsState()
    val totalWeek by dashboardVm.totalWeekFlow.collectAsState()

    LaunchedEffect(Unit) {
        dashboardVm.loadTodayUsage()
        dashboardVm.loadWeeklyUsage()
    }

    // TODO: Make a pull to refresh to reload usage stats
    LaunchedEffect(todayUsage,weeklyUsage) {
        Log.d("DashboardScreen", "Today Usage: $todayUsage")
        Log.d("DashboardScreen", "Weekly Usage: $weeklyUsage")
        Log.d("DashboardScreen", "Total Today: $totalToday")
        Log.d("DashboardScreen", "Total Week: $totalWeek")
    }

    val (rangeFilter, setRangeFilter) = remember { mutableStateOf(RangeFilter.WEEK) }

    val rangeLabel = when (rangeFilter) {
        RangeFilter.DAY -> "Today"
        RangeFilter.WEEK -> "This Week"
    }

    // Aggregate weekly usage by app
    val weeklyTotalsByApp: Map<String, Long> = weeklyUsage
        .flatMap { (_, perApp) -> perApp.entries }
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, values) -> values.sum() }

    // Choose data source based on selected range
    val appTotalsForRange: Map<String, Long> =
        if (rangeFilter == RangeFilter.DAY) todayUsage else weeklyTotalsByApp

    val allAppsList: List<AppUsage> = appTotalsForRange.entries
        .sortedByDescending { it.value }
        .map { (pkg, millis) ->
            AppUsage(
                appName = pkg.substringAfterLast('.'),
                usageMillis = millis
            )
        }

    val mostUsedEntry = appTotalsForRange.maxByOrNull { it.value }
    val mostUsedAppName = mostUsedEntry?.key?.substringAfterLast('.')
    val mostUsedAppMillis = mostUsedEntry?.value ?: 0L

    val totalDayMillis = totalToday
    val totalWeekMillis = totalWeek
    val totalMillisForRange = if (rangeFilter == RangeFilter.DAY) totalDayMillis else totalWeekMillis

    // Build bar data for the chart (daily or weekly)
    val (bars, averageMinutes) =
        if (rangeFilter == RangeFilter.DAY) {
            val minutes = (totalDayMillis / 60_000L).toInt()
            val bar = DailyAverageBar(
                label = "T",
                heightFraction = 1f
            )
            listOf(bar) to minutes
        } else {
            val orderedDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val perDayTotalMinutes: List<Pair<String, Int>> = orderedDays.map { day ->
                val millis = weeklyUsage[day]?.values?.sum() ?: 0L
                val minutes = (millis / 60_000L).toInt()
                day to minutes
            }

            val maxMinutesInWeek =
                perDayTotalMinutes.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
            val weeklyBars: List<DailyAverageBar> = perDayTotalMinutes.map { (label, minutes) ->
                DailyAverageBar(
                    label = label.first().toString(),
                    heightFraction = (minutes.toFloat() / maxMinutesInWeek).coerceIn(0f, 1f)
                )
            }

            val avgMinutes =
                if (perDayTotalMinutes.isNotEmpty()) perDayTotalMinutes.sumOf { it.second } / perDayTotalMinutes.size
                else 0

            weeklyBars to avgMinutes
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.padding(25.dp))
        TopBar(
            selectedTab = if (rangeFilter == RangeFilter.WEEK) "Week" else "Day",
            onTabSelected = { tab ->
                setRangeFilter(if (tab == "Day") RangeFilter.DAY else RangeFilter.WEEK)
            },
            onConnectClick = {
                studentVm.loadPairingCode { generatedCode ->
                    val encoded = Base64.getUrlEncoder().encodeToString(generatedCode.toByteArray())
                    val route = Routes.ScanQR.route.replace("{pairing_code}", encoded)
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            },
            onLogoutClick = {
                authVm.logout {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                    }
                }
            },
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                DailyAverageCard(
                    averageMinutes = averageMinutes,
                    bars = bars,
                )
                MostUsedAppCard(
                    appName = mostUsedAppName,
                    appUsageMillis = mostUsedAppMillis,
                    totalUsageMillis = totalMillisForRange,
                    rangeLabel = rangeLabel
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                AllAppsCard(
                    title = if (rangeFilter == RangeFilter.DAY) "All Apps (Today)" else "All Apps (This Week)",
                    apps = allAppsList
                )
            }
        }
    }
}