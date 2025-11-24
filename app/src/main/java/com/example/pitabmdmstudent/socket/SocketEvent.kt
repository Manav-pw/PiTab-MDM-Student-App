package com.example.pitabmdmstudent.socket

sealed class SocketEvent {
    object Connected : SocketEvent()
    object Disconnected : SocketEvent()

    data class HMSVideoCall(val token: String): SocketEvent()
    data class HMSAudioCall(val token: String): SocketEvent()
    data class HMSScreenShare(val token: String): SocketEvent()
    data class VideoCallEnd(val ended: Boolean): SocketEvent()
}