package com.example.pitabmdmstudent.data.remote.api

import com.example.pitabmdmstudent.data.remote.network.ApiResponse
import com.example.pitabmdmstudent.models.auth.DeviceLoginRequest
import com.example.pitabmdmstudent.models.auth.DeviceLoginRes
import com.example.pitabmdmstudent.models.auth.UpdateNameRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface DeviceAuthApi {

    @POST("mdm/auth/device")
    suspend fun loginDevice(
        @Body body: DeviceLoginRequest,
    ): Response<ApiResponse<DeviceLoginRes>>

    @PATCH("mdm/auth/update")
    suspend fun updateName(
        @Body body: UpdateNameRequest,
    ): Response<ApiResponse<Unit>>
}


