package com.example.pitabmdmstudent.data.datasource

import com.example.pitabmdmstudent.models.request.AppInfoRequest
import com.example.pitabmdmstudent.models.request.AppUsageStatsRequest
import com.example.pitabmdmstudent.data.network.ApiResponse
import com.example.pitabmdmstudent.models.request.DeviceStateRequest
import com.example.pitabmdmstudent.models.request.SendScreenshotRequest
import retrofit2.Response

interface StudentDataSource {
    suspend fun getPairingCode(): Response<ApiResponse<String>>

    suspend fun uploadAppList(
        request: List<AppInfoRequest>
    ): Response<ApiResponse<Unit>>

    suspend fun updateDeviceState(deviceStateRequest: DeviceStateRequest): Response<ApiResponse<Unit>>

    suspend fun postAppUsageStats(request: AppUsageStatsRequest): Response<ApiResponse<List<Any>>>

    suspend fun sendScreenshot(
        pairingId: String,
        sendScreenshotBody: SendScreenshotRequest
    ): Response<ApiResponse<Unit>>
}
