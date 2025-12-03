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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Base64
import java.util.Locale

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

    val today = LocalDate.now()
    val todayUsageMillis: Long = weeklyUsage[today.toString()]?.values?.sum() ?: 0L

    val dailyBar = DailyAverageBar(
        label = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).first().toString(),
        heightFraction = 1f // single bar full height
    )

    val appListToday: List<AppUsage> = weeklyUsage[today.toString()]?.map { (pkg, millis) ->
        AppUsage(pkg.substringAfterLast('.'), millis)
    }?.sortedByDescending { it.usageMillis } ?: emptyList()

    val weeklyTotalsPerDay = dashboardVm.getWeeklyTotalsPerDay(weeklyUsage)

    val maxMillis = weeklyTotalsPerDay.values.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val weeklyBars = weeklyTotalsPerDay.map { (day, totalMillis) ->
        DailyAverageBar(
            label = day.first().toString(),
            heightFraction = (totalMillis.toFloat() / maxMillis.toFloat()).coerceIn(0f, 1f)
        )
    }

// All apps aggregated across week
    val allAppsWeekly: List<AppUsage> = weeklyUsage
        .flatMap { (_, perApp) -> perApp.entries }
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, values) -> values.sum() }
        .entries
        .sortedByDescending { it.value }
        .map { (pkg, millis) -> AppUsage(pkg.substringAfterLast('.'), millis) }

    val (rangeFilter, setRangeFilter) = remember { mutableStateOf(RangeFilter.WEEK) }


    LaunchedEffect(rangeFilter) {
        Log.d("Dashboard", "updated values")
        dashboardVm.loadTodayUsage()
        dashboardVm.loadWeeklyUsage()
    }

    // TODO: Make a pull to refresh to reload usage stats
    LaunchedEffect(todayUsage,weeklyUsage) {
        Log.d("DashboardScreen", "Today Usage: $todayUsage")
        Log.d("DashboardScreen", "Weekly Usage: $weeklyUsage")
        Log.d("DashboardScreen", "Total Today: $totalToday")
        Log.d("DashboardScreen", "Total Week: $totalWeek")
        Log.d("DashboardScreen", "TodayUsageMillis: $todayUsageMillis")

    }


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

//    val allAppsList: List<AppUsage> = appTotalsForRange.entries
//        .sortedByDescending { it.value }
//        .map { (pkg, millis) ->
//            AppUsage(
//                appName = pkg.substringAfterLast('.'),
//                usageMillis = millis
//            )
//        }

    val mostUsedEntry = appTotalsForRange.maxByOrNull { it.value }
    val mostUsedAppName = mostUsedEntry?.key?.substringAfterLast('.')
    val mostUsedAppMillis = mostUsedEntry?.value ?: 0L

    val totalDayMillis = totalToday
    val totalWeekMillis = totalWeek
    val totalMillisForRange = if (rangeFilter == RangeFilter.DAY) totalDayMillis else totalWeekMillis

    val (bars, allAppsList, averageMinutes) =
        remember(todayUsageMillis, weeklyUsage, totalToday, totalWeek, rangeFilter) {
            if (rangeFilter == RangeFilter.DAY) {
            Triple(
                listOf(dailyBar),
                appListToday,
                (todayUsageMillis / 60_000L).toInt()
            )
            } else {
                Triple(
                    weeklyBars,
                    allAppsWeekly,
                    (weeklyTotalsPerDay.values.sum() / 60_000L / 7).toInt()
                )
            }
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