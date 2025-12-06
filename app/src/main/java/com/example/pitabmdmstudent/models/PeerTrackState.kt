package com.example.pitabmdmstudent.models

import live.hms.video.media.tracks.HMSVideoTrack
import live.hms.video.sdk.models.HMSPeer

data class PeerTrackState(
    val peer: HMSPeer,
    val videoTrack: HMSVideoTrack?,       // null if camera is OFF
    val screenTrack: HMSVideoTrack?,      // screen share track (optional)
)