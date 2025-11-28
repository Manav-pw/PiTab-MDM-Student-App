package com.example.pitabmdmstudent.models.request

data class SendScreenshotRequest(
    val screenshotBase64: String,
    val pairingId: String
)
