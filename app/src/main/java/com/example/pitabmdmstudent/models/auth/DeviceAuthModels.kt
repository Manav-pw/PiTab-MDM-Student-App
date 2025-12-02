package com.example.pitabmdmstudent.models.auth

data class DeviceLoginRequest(
    val phone: Long,
    val deviceOS: String,
    val machineId: String,
)

data class DeviceUserInfo(
    val name: String?,
    val phone: Long?,
    val role: String?,
    val deviceOS: String?,
)

data class DeviceLoginRes(
    val success: Boolean,
    val socketToken: String,
    val userId: String,
    val usbEnableRequestRaised: Boolean,
    val user: DeviceUserInfo?,
)

data class UpdateNameRequest(
    val name: String,
)


