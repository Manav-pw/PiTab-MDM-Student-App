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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pitabmdmstudent.models.AppUsage

@Composable
fun AllAppsCard(apps: List<AppUsage>) {
    DashboardCard(modifier = Modifier.fillMaxHeight()) {
        Text(
            "All Apps (This Week)",
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

@Composable
fun AppUsageRow(app: AppUsage) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        CircleAvatar(app.appName.first().uppercase())

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(app.appName, color = Color.White)
            LinearProgressIndicator(
                progress = (app.usageMinutes / 60f).coerceIn(0f, 1f),
                color = Color(0xFF1E8CFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        }

        Spacer(Modifier.width(12.dp))

        Text("${app.usageMinutes}m", color = Color.White)
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
