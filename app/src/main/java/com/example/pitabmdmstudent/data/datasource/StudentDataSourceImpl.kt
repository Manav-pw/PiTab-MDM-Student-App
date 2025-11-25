package com.example.pitabmdmstudent.data.datasource

import com.example.pitabmdmstudent.data.api.StudentApi
import com.example.pitabmdmstudent.models.request.AppInfoRequest
import com.example.pitabmdmstudent.models.request.AppUsageStatsRequest
import com.example.pitabmdmstudent.data.network.ApiResponse
import com.example.pitabmdmstudent.models.request.DeviceStateRequest
import retrofit2.Response
import javax.inject.Inject

class StudentDataSourceImpl @Inject constructor(
    private val api: StudentApi
) : StudentDataSource {

    override suspend fun getPairingCode(): Response<ApiResponse<String>> =
        api.getPairingCode()

    override suspend fun uploadAppList(request: List<AppInfoRequest>): Response<ApiResponse<Unit>> {
        return api.uploadAppList(request)
    }

    override suspend fun updateDeviceState(deviceStateRequest: DeviceStateRequest): Response<ApiResponse<Unit>> {
        return api.updateDeviceState(deviceStateRequest)
    }



    override suspend fun postAppUsageStats(request: AppUsageStatsRequest): Response<ApiResponse<List<Any>>> {
        return api.postAppUsageStats(request)
    }
}