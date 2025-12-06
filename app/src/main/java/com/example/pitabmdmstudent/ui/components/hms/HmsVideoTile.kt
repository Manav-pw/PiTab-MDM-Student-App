package com.example.pitabmdmstudent.ui.components.hms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import hms.webrtc.RendererCommon
import live.hms.video.media.tracks.HMSVideoTrack
import live.hms.videoview.HMSVideoView

@Composable
fun HmsVideoTile(
    modifier: Modifier = Modifier,
    track: HMSVideoTrack?,
    scaleType: RendererCommon.ScalingType = RendererCommon.ScalingType.SCALE_ASPECT_BALANCED
) {
    val context = LocalContext.current

    // Create the HMSVideoView programmatically
    val videoView = remember {
        HMSVideoView(context).apply {
            setScalingType(scaleType)
        }
    }

    if (track != null) {
        AndroidView(
            modifier = modifier,
            factory = { videoView },
            update = { view ->
                try {
                    track.addSink(view)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        )

        // Cleanup when the composable leaves the screen or track changes
        DisposableEffect(track) {
            onDispose {
                try {
                    track.removeSink(videoView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}