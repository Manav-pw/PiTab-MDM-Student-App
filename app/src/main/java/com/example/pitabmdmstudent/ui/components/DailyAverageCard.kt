package com.example.pitabmdmstudent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DailyAverageBar(
    val label: String,
    val heightFraction: Float
)

@Composable
fun DailyAverageCard(
    averageMinutes: Int,
    bars: List<DailyAverageBar>,
) {
    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${averageMinutes}m",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Daily Average",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val safeBars = if (bars.isEmpty()) {
                    // Fallback dummy bars when no data is available
                    listOf(
                        DailyAverageBar("S", 0f),
                        DailyAverageBar("M", 0f),
                        DailyAverageBar("T", 0f),
                        DailyAverageBar("W", 0f),
                        DailyAverageBar("T", 0f),
                        DailyAverageBar("F", 0f),
                        DailyAverageBar("S", 0f),
                    )
                } else {
                    bars
                }

                safeBars.forEach { bar ->
                    Bar(day = bar.label, heightFraction = bar.heightFraction)
                }
            }
        }
    }
}

@Composable
fun Bar(day: String, heightFraction: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(100.dp * heightFraction)
                .fillMaxWidth(0.1f)
                .background(Color.Blue, RoundedCornerShape(4.dp))
        )
        Text(
            text = day,
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}