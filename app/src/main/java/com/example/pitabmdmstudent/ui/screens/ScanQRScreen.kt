package com.example.pitabmdmstudent.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pitabmdmstudent.data.viewmodel.ScanQRViewModel

@Composable
fun ScanQRScreen(
    qrVm: ScanQRViewModel = hiltViewModel(),
    navController: NavController,
    pairingCode: String? = null
) {
    val qrBitmap by qrVm.qrBitmap.collectAsState()

    LaunchedEffect(pairingCode) {
        if (pairingCode != null) {
            qrVm.setCode(pairingCode)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Gradient Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF5574D2),
                            Color(0xFF3B60D1),
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.45f)
                .background(Color(0xFF0F172A))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "back", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Scan this QR using your parent app",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Device Pairing QR",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF333333)
                )

                Spacer(Modifier.height(32.dp))

                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(260.dp)
                    )
                } else {
                    CircularProgressIndicator(color = Color(0xFF5574D2))
                }
            }
        }
    }
}
