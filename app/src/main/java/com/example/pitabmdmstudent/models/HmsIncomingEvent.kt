package com.example.pitabmdmstudent.models

data class HmsIncomingEvent (
    val callId: String,
    val token: String,
    val parentName: String,
    val callType: String
)