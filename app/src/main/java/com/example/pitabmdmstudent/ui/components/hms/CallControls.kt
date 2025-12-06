package com.example.pitabmdmstudent.ui.components.hms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CallControls(
    modifier: Modifier = Modifier,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onSwitchCamera: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        IconButton(onClick = onToggleMute) {
            Icon(Icons.Default.MicOff, "Mute")
        }
        IconButton(onClick = onSwitchCamera) {
            Icon(Icons.Default.Cameraswitch, "Switch")
        }
        IconButton(onClick = onEndCall) {
            Icon(Icons.Default.CallEnd, "End")
        }
    }
}
