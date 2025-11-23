package com.example.pitabmdmstudent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar() {
    var selectedTab by remember { mutableStateOf("Week") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "My Dashboard",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF1E1E2E))
        ) {
            DashboardToggle("Week", selectedTab == "Week") {
                selectedTab = "Week"
            }
            DashboardToggle("Day", selectedTab == "Day") {
                selectedTab = "Day"
            }
        }

        ConnectButton()
    }
}

@Composable
fun DashboardToggle(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ConnectButton() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E2E))
            .clickable { /* TODO */ }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(Modifier.width(8.dp))
        Text("Scan to Connect", color = Color.White)
    }
}