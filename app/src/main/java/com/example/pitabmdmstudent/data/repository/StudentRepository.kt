package com.example.pitabmdmstudent.data.repository

import com.example.pitabmdmstudent.data.datasource.StudentDataSource
import com.example.pitabmdmstudent.models.request.AppInfoRequest
import com.example.pitabmdmstudent.models.request.AppUsageStatsRequest
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

    suspend fun updateDeviceState(deviceStateRequest: com.example.pitabmdmstudent.models.request.DeviceStateRequest): Boolean {
        val response = studentDataSource.updateDeviceState(deviceStateRequest)
        return response.isSuccessful && (response.body()?.success == true)
    }

    suspend fun uploadInstalledApps(apps: List<AppInfoRequest>): Boolean {
        val response = studentDataSource.uploadAppList(apps)
        return response.isSuccessful
    }

    suspend fun postAppUsageStats(appUsageRequest: AppUsageStatsRequest): Boolean {
        val response = studentDataSource.postAppUsageStats(appUsageRequest)
        return response.isSuccessful && (response.body()?.success == true)
    }

    suspend fun sendScreenshot(
        pairingId: String,
        sendScreenshotRequest: com.example.pitabmdmstudent.models.request.SendScreenshotRequest
    ): Boolean {
        val response = studentDataSource.sendScreenshot(pairingId, sendScreenshotRequest)
        return response.isSuccessful && (response.body()?.success == true)
    }
}