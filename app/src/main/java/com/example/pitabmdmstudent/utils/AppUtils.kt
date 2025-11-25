package com.example.pitabmdmstudent.utils

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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

    fun getBatteryLevel(context: Context): Int {
        val bm = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun isCharging(context: Context): Boolean {
        val intent = ContextWrapper(context)
            .registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }
}