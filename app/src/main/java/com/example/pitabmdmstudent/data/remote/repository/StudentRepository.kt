package com.example.pitabmdmstudent.data.remote.repository

import android.util.Log
import com.example.pitabmdmstudent.data.remote.datasource.StudentDataSource
import com.example.pitabmdmstudent.models.request.AppInfoRequest
import com.example.pitabmdmstudent.models.request.AppUsageStatsRequest
import com.example.pitabmdmstudent.models.request.CallLogRequest
import com.example.pitabmdmstudent.models.request.DeviceStateRequest
import com.example.pitabmdmstudent.models.request.SendScreenshotRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val studentDataSource: StudentDataSource
) {
    suspend fun getPairingCode(): String? {
        val response = studentDataSource.getPairingCode()

        return if (response.isSuccessful) {
            response.body()?.data
        } else {
            null
        }
    }

    suspend fun updateDeviceState(deviceStateRequest: DeviceStateRequest): Boolean {
        val response = studentDataSource.updateDeviceState(deviceStateRequest)
        return response.isSuccessful && (response.body()?.success == true)
    }

    suspend fun uploadInstalledApps(apps: List<AppInfoRequest>): Boolean {
        val response = studentDataSource.uploadAppList(apps)
        return response.isSuccessful
    }

    suspend fun sendScreenshot(
        pairingId: String,
        sendScreenshotRequest: SendScreenshotRequest
    ): Boolean {
        val response = studentDataSource.sendScreenshot(pairingId, sendScreenshotRequest)
        Log.d("ScreenShotTest", "response api: $response")
        return response.isSuccessful && (response.body()?.success == true)
    }

    suspend fun postCallLogs(request: CallLogRequest): Boolean {
        return try {
            val response = studentDataSource.postCallLogs(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    suspend fun postAppUsageStats(request: AppUsageStatsRequest): Boolean {
        return try {
            val response = studentDataSource.postAppUsageStats(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}