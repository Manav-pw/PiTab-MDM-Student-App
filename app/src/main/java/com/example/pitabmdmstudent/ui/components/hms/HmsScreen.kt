package com.example.pitabmdmstudent.ui.components.hms

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pitabmdmstudent.data.viewmodel.HmsViewModel
import hms.webrtc.RendererCommon
@Composable
fun HmsScreen(
    viewModel: HmsViewModel,
    onEndCall: () -> Unit
) {
    val participants = viewModel.peerTrackState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        if (participants.value.isEmpty()) {
            // Waiting UI
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("Joining room...", color = Color.White)
            }
        } else {
            // If someone is screen-sharing, show that full screen
            val screenShare = participants.value.firstOrNull { it.screenTrack != null }

            if (screenShare != null) {
                HmsVideoTile(
                    modifier = Modifier.fillMaxSize(),
                    track = screenShare.screenTrack!!,
                    scaleType = RendererCommon.ScalingType.SCALE_ASPECT_FIT
                )
            } else {
                // Otherwise show grid of video tracks
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(participants.value) { item ->
                        if (item.videoTrack != null) {
                            HmsVideoTile(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f),
                                track = item.videoTrack,
                                scaleType = RendererCommon.ScalingType.SCALE_ASPECT_FILL
                            )
                        } else {
                            // Video off
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.peer.name, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Local PIP (optional)
            val localPeer = participants.value.firstOrNull { it.peer.isLocal }
            localPeer?.videoTrack?.let { localTrack ->
                HmsVideoTile(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp, 80.dp)
                        .size(140.dp),
                    track = localTrack,
                    scaleType = RendererCommon.ScalingType.SCALE_ASPECT_FILL
                )
            }
        }
    }
}
