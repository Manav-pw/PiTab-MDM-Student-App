package com.example.pitabmdmstudent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IncomingCallScreen(
    callerName: String,
    callType: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Incoming $callType call",
                color = Color.White.copy(0.8f),
                fontSize = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = callerName,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(50.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                FloatingActionButton(
                    containerColor = Color.Red,
                    onClick = onReject
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Reject")
                }
                FloatingActionButton(
                    containerColor = Color.Green,
                    onClick = onAccept
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Accept")
                }
            }
        }
    }
}
