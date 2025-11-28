package com.example.pitabmdmstudent.data.remote.api

import com.example.pitabmdmstudent.BuildConfig
import com.example.pitabmdmstudent.models.request.AppInfoRequest
import com.example.pitabmdmstudent.models.request.AppUsageStatsRequest
import com.example.pitabmdmstudent.data.remote.network.ApiResponse
import com.example.pitabmdmstudent.models.request.DeviceStateRequest
import com.example.pitabmdmstudent.models.request.SendScreenshotRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudentApi {
    @GET("${BuildConfig.PARENT_BASE_URL}mdm/device-pairing/generate")
    suspend fun getPairingCode(): Response<ApiResponse<String>>

    @POST("${BuildConfig.PARENT_BASE_URL}/mdm/generic-app-management/app-block-rule/set-installed-apps")
    suspend fun uploadAppList(
        @Body uploadAppListRequest: List<AppInfoRequest>
    ): Response<ApiResponse<Unit>>

    @POST("${BuildConfig.PARENT_BASE_URL}/mdm/device-state/update")
    suspend fun updateDeviceState(
        @Body deviceState: DeviceStateRequest
    ): Response<ApiResponse<Unit>>

    @POST("${BuildConfig.PARENT_BASE_URL}mdm/app-management/device-usage")
    suspend fun postAppUsageStats(
        @Body appUsageStatsRequest: AppUsageStatsRequest
    ): Response<ApiResponse<List<Any>>>

    @POST("${BuildConfig.PARENT_BASE_URL}/mdm/connect/screenshot/{pairingId}")
    suspend fun sendScreenshot(
        @Path("pairingId") pairingId: String,
        @Body sendScreenshotBody: SendScreenshotRequest
    ): Response<ApiResponse<Unit>>

}