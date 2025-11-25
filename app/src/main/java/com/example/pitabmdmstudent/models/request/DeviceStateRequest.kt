package com.example.pitabmdmstudent.models.request

data class DeviceStateRequest(
    val visibleApps: List<VisibleApp>,
    val batteryReading: String?,
    val batteryCharging: Boolean?
)

data class VisibleApp(
    val packageName: String,
    val applicationName: String
)