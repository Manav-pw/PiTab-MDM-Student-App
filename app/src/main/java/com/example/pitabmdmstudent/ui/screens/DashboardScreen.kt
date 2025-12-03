package com.example.pitabmdmstudent.ui.screens

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pitabmdmstudent.R
import com.example.pitabmdmstudent.data.remote.viewModel.AuthViewModel
import com.example.pitabmdmstudent.data.remote.viewModel.StudentViewModel
import com.example.pitabmdmstudent.data.viewmodel.DashboardViewModel
import com.example.pitabmdmstudent.models.AppUsage
import com.example.pitabmdmstudent.navigation.Routes
import com.mikepenz.hypnoticcanvas.shaderBackground
import com.mikepenz.hypnoticcanvas.shaders.MeshGradient
import java.util.Base64

// Dark Theme Colors - matching LoginScreen
private val DarkBackground = Color(0xFF161E26)
private val DarkSurface = Color(0xFF1E2832)
private val DarkSurfaceVariant = Color(0xFF2A3642)
private val PrimaryAccent = Color(0xFF3388D7)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFB0BEC5)
private val TextMuted = Color(0xFF78909C)
private val ErrorColor = Color(0xFFEF5350)
private val ShapeColor = Color(0xFF3388D7)
private val SuccessColor = Color(0xFF4CAF50)
private val WarningColor = Color(0xFFFFB74D)

private enum class RangeFilter { DAY, WEEK }

// Mesh gradient colors for background
private val MeshGradientColor1 = Color(0xFF141414)
private val MeshGradientColor2 = Color(0xFF1B2E40)

// Modern Dashboard Card
@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (title != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = PrimaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                }
            }
            content()
        }
    }
}

// Header/TopBar Component
@Composable
private fun DashboardHeader(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onConnectClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isLandscape: Boolean
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    
    if (isLandscape) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding.calculateTopPadding() + 8.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Left - Title
            Text(
                text = "Dashboard",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // Center - Toggle (absolutely centered on screen)
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TogglePillLandscape(
                        text = "Week",
                        selected = selectedTab == "Week",
                        onClick = { onTabSelected("Week") }
                    )
                    TogglePillLandscape(
                        text = "Day",
                        selected = selectedTab == "Day",
                        onClick = { onTabSelected("Day") }
                    )
                }
            }

            // Right - Action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                // Connect Button
                Surface(
                    onClick = onConnectClick,
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connect",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        )
                    }
                }

                // Logout Button
                Surface(
                    onClick = onLogoutClick,
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = ErrorColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Portrait Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding.calculateTopPadding() + 8.dp)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title only (no logo)
                Text(
                    text = "Dashboard",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                // Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Connect Button with label
                    Surface(
                        onClick = onConnectClick,
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = PrimaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Connect",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                    // Logout Button
                    Surface(
                        onClick = onLogoutClick,
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = ErrorColor,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle Pills (full width in portrait)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    TogglePill(
                        text = "This Week",
                        selected = selectedTab == "Week",
                        onClick = { onTabSelected("Week") },
                        modifier = Modifier.weight(1f)
                    )
                    TogglePill(
                        text = "Today",
                        selected = selectedTab == "Day",
                        onClick = { onTabSelected("Day") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TogglePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (selected) PrimaryAccent else Color.Transparent,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selected) TextPrimary else TextSecondary
                )
            )
        }
    }
}

// Landscape toggle pill - centered in header with proper dimensions
@Composable
private fun TogglePillLandscape(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (selected) PrimaryAccent else Color.Transparent,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selected) TextPrimary else TextSecondary
                )
            )
        }
    }
}

// Usage Stats Card with Bar Chart
@Composable
private fun UsageStatsCard(
    averageMinutes: Int,
    bars: List<Pair<String, Float>>,
    rangeLabel: String,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        modifier = modifier,
        title = "Screen Time",
        icon = Icons.Default.BarChart
    ) {
        // Main stat
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = formatMinutesToDisplay(averageMinutes),
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (rangeLabel == "Today") "today" else "avg/day",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextSecondary
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bar Chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { (label, fraction) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height((80.dp * fraction.coerceAtLeast(0.05f)))
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (fraction > 0.7f) WarningColor
                                else if (fraction > 0.4f) PrimaryAccent
                                else PrimaryAccent.copy(alpha = 0.6f)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    )
                }
            }
        }
    }
}

// Most Used App Card
@Composable
private fun MostUsedCard(
    appName: String?,
    appUsageMillis: Long,
    totalUsageMillis: Long,
    rangeLabel: String,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        modifier = modifier,
        title = "Most Used",
        icon = Icons.Default.Star
    ) {
        // App name and icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (appName?.firstOrNull()?.toString() ?: "?").uppercase(),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryAccent
                    )
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = appName ?: "No usage yet",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rangeLabel,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                )
            }
        }

        // Stats Row - Larger cards for App Time and Total Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = Icons.Default.Timer,
                value = formatDuration(appUsageMillis),
                label = "App Time",
                iconTint = WarningColor,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.AccessTime,
                value = formatDuration(totalUsageMillis),
                label = "Total Time",
                iconTint = SuccessColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                )
            }
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }
    }
}

// All Apps Card (Portrait - scrollable content)
@Composable
private fun AllAppsCard(
    apps: List<AppUsage>,
    rangeLabel: String,
    modifier: Modifier = Modifier
) {
    DashboardCard(
        modifier = modifier,
        title = "All Apps",
        icon = Icons.Default.Apps
    ) {
        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No app usage data yet",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                apps.take(10).forEach { app ->
                    AppUsageItem(app = app, maxMillis = apps.maxOfOrNull { it.usageMillis } ?: 1L)
                }
            }
        }
    }
}

// All Apps Card for Landscape - fills full height with scrollable LazyColumn
@Composable
private fun AllAppsCardLandscape(
    apps: List<AppUsage>,
    rangeLabel: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "All Apps",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
            }

            // Apps list - fills remaining space
            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No app usage data yet",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apps) { app ->
                        AppUsageItem(app = app, maxMillis = apps.maxOfOrNull { it.usageMillis } ?: 1L)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppUsageItem(
    app: AppUsage,
    maxMillis: Long
) {
    val progress = (app.usageMillis.toFloat() / maxMillis.coerceAtLeast(1L)).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.appName.first().toString().uppercase(),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // App name and progress
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = app.appName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
                Text(
                    text = formatDuration(app.usageMillis),
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryAccent,
                trackColor = DarkSurfaceVariant,
            )
        }
    }
}

// Utility functions
private fun formatDuration(millis: Long): String {
    val totalMinutes = millis / 60_000L
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "<1m"
    }
}

private fun formatMinutesToDisplay(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 -> "${hours}h ${mins}m"
        else -> "${mins}m"
    }
}

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

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Set status bar to light icons (for dark background)
    val activity = context as? Activity
    activity?.let {
        val window = it.window
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
    }

    LaunchedEffect(Unit) {
        dashboardVm.loadTodayUsage()
        dashboardVm.loadWeeklyUsage()
    }

    LaunchedEffect(todayUsage, weeklyUsage) {
        Log.d("DashboardScreen", "Today Usage: $todayUsage")
        Log.d("DashboardScreen", "Weekly Usage: $weeklyUsage")
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

    // Build bar data for the chart
    val (bars, averageMinutes) =
        if (rangeFilter == RangeFilter.DAY) {
            val minutes = (totalDayMillis / 60_000L).toInt()
            listOf("Now" to 1f) to minutes
        } else {
            val orderedDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            val perDayTotalMinutes: List<Pair<String, Int>> = orderedDays.map { day ->
                val millis = weeklyUsage[day]?.values?.sum() ?: 0L
                val minutes = (millis / 60_000L).toInt()
                day to minutes
            }

            val maxMinutesInWeek =
                perDayTotalMinutes.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
            val weeklyBars: List<Pair<String, Float>> = perDayTotalMinutes.map { (label, minutes) ->
                label.take(1) to (minutes.toFloat() / maxMinutesInWeek).coerceIn(0f, 1f)
            }

            val avgMinutes =
                if (perDayTotalMinutes.isNotEmpty()) perDayTotalMinutes.sumOf { it.second } / perDayTotalMinutes.size
                else 0

            weeklyBars to avgMinutes
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shaderBackground(
                MeshGradient(
                    colors = arrayOf(MeshGradientColor1, MeshGradientColor2, MeshGradientColor1, MeshGradientColor1, MeshGradientColor1, MeshGradientColor2),
                    scale = 1.5f
                )
            )
    ) {

        if (isLandscape) {
            // Landscape Layout - scrollable to handle content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                DashboardHeader(
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
                    isLandscape = true
                )

                // Content Row - both columns take natural content height
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column - Stats Cards
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        UsageStatsCard(
                            averageMinutes = averageMinutes,
                            bars = bars,
                            rangeLabel = rangeLabel
                        )
                        MostUsedCard(
                            appName = mostUsedAppName,
                            appUsageMillis = mostUsedAppMillis,
                            totalUsageMillis = totalMillisForRange,
                            rangeLabel = rangeLabel
                        )
                    }

                    // Right Column - All Apps (same natural height, scrolls internally if needed)
                    AllAppsCard(
                        apps = allAppsList,
                        rangeLabel = rangeLabel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Portrait Layout - Stacked Cards
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                DashboardHeader(
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
                    isLandscape = false
                )

                // Cards stacked vertically
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    UsageStatsCard(
                        averageMinutes = averageMinutes,
                        bars = bars,
                        rangeLabel = rangeLabel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    MostUsedCard(
                        appName = mostUsedAppName,
                        appUsageMillis = mostUsedAppMillis,
                        totalUsageMillis = totalMillisForRange,
                        rangeLabel = rangeLabel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    AllAppsCard(
                        apps = allAppsList,
                        rangeLabel = rangeLabel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
