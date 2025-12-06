package com.example.pitabmdmstudent.socket

import com.example.pitabmdmstudent.models.HmsIncomingEvent

sealed class SocketEvent {
    object Connected : SocketEvent()
    object Disconnected : SocketEvent()
    data class ScreenshotRequest(val pairingId: String) : SocketEvent()

    data class HMSVideoCall(val hmsData: HmsIncomingEvent): SocketEvent()
    data class HMSAudioCall(val hmsData: HmsIncomingEvent): SocketEvent()
    data class HMSScreenShare(val hmsData: HmsIncomingEvent): SocketEvent()
    data class VideoCallEnd(val hmsData: HmsIncomingEvent): SocketEvent()
}