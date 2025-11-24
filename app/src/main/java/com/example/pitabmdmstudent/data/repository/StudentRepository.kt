package com.example.pitabmdmstudent.data.repository

import android.util.Log
import com.example.pitabmdmstudent.data.datasource.StudentDataSource
import com.example.pitabmdmstudent.models.request.AppInfoRequest
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

    suspend fun uploadInstalledApps(apps: List<AppInfoRequest>): Boolean {
        val response = studentDataSource.uploadAppList(apps)
        return response.isSuccessful
    }
}