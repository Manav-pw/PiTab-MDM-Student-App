package com.example.pitabmdmstudent.utils

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Context.LAUNCHER_APPS_SERVICE
import android.content.Context.USER_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.UserManager
import android.util.Log
import com.example.pitabmdmstudent.models.request.AppInfoRequest

object AppUtils {
//    fun getInstalledApps(context: Context): List<AppInfoRequest> {
//        val launcherApps = context.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
//        val unfilteredList: MutableList<LauncherActivityInfo> = java.util.ArrayList()
//
//        val userManager = context.getSystemService(USER_SERVICE) as UserManager
//        val userHandles = userManager.userProfiles
//
//        for (handle in userHandles) {
//            unfilteredList.addAll(launcherApps.getActivityList(null, handle))
//        }
//
//        var appList = unfilteredList.map {
//            AppInfoRequest(
//                appName = it.label.toString(),
//                packageName = it.applicationInfo.packageName,
//            )
//        }
//
//        Log.d("APP_LIST","$appList")
//
//        return appList
//    }

    fun getAllInstalledApps(context: Context): List<AppInfoRequest> {
        val pm = context.packageManager

        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        Log.d("APP_LIST","$apps")
        return apps.map {
            AppInfoRequest(
                appName = pm.getApplicationLabel(it).toString(),
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

    fun getAppName(context: Context, packageName: String): String {
        val packageManager = context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }
}