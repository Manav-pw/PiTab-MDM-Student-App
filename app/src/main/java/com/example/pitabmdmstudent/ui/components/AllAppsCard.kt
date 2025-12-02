package com.example.pitabmdmstudent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import com.example.pitabmdmstudent.models.AppUsage

@Composable
fun AllAppsCard(
    title: String,
    apps: List<AppUsage>
) {
    DashboardCard(modifier = Modifier.fillMaxHeight()) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            Text(
                title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(16.dp))

            apps.forEach { app ->
                AppUsageRow(app)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AppUsageRow(app: AppUsage) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        CircleAvatar(app.appName.first().uppercase())

        Spacer(Modifier.width(12.dp))

        val totalMinutes = (app.usageMillis / 60_000L).toFloat()
        val displayText = formatDuration(app.usageMillis)
        val progress = (totalMinutes / 60f).coerceIn(0f, 1f) // full bar at 60 minutes

        Column(modifier = Modifier.weight(1f)) {
            Text(app.appName, color = Color.White)
            LinearProgressIndicator(
                progress = progress,
                color = Color(0xFF1E8CFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(displayText, color = Color.White)
    }
}

@Composable
fun CircleAvatar(letter: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFF3D4B61)),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return if (minutes >= 1) {
        "${minutes}m"
    } else {
        "${seconds} sec"
    }
}
