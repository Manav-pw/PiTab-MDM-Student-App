package com.example.pitabmdmstudent.utils

import android.content.Context
import com.example.pitabmdmstudent.models.request.AppInfoRequest

object AppUtils {
    fun getInstalledApps(context: Context): List<AppInfoRequest> {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(0)

        return packages.map {
            AppInfoRequest(
                appName = it.loadLabel(packageManager).toString(),
                packageName = it.packageName
            )
        }
    }
}