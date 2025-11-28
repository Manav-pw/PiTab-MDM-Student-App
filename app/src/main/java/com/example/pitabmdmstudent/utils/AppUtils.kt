package com.example.pitabmdmstudent.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Context.LAUNCHER_APPS_SERVICE
import android.content.Context.USER_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.UserManager
import android.util.Log
import com.example.pitabmdmstudent.models.AppUsageDetails
import com.example.pitabmdmstudent.models.request.AppInfoRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppUtils {
    fun getInstalledApps(context: Context): List<AppInfoRequest> {
        val launcherApps = context.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
        val unfilteredList: MutableList<LauncherActivityInfo> = java.util.ArrayList()

        val userManager = context.getSystemService(USER_SERVICE) as UserManager
        val userHandles = userManager.userProfiles

        for (handle in userHandles) {
            unfilteredList.addAll(launcherApps.getActivityList(null, handle))
        }

        return unfilteredList.map {
            AppInfoRequest(
                appName = it.label.toString(),
                packageName = it.applicationInfo.packageName,
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

    fun getUsageStats(usageStatsManager: UsageStatsManager, context: Context): List<AppUsageDetails> {
        val end = System.currentTimeMillis()
        val start = end - (5 * 60 * 1000) // last 5 minutes

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end
        ) ?: return emptyList()

        val pm = context.packageManager
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'", Locale.US)

        val launcherPackages = getLauncherApps(context)

        return stats
            .filter { it.totalTimeInForeground > 0 }
            .filter { it.packageName !in launcherPackages }
            .map { usage ->
                val pkg = usage.packageName
                val appName = try {
                    val info = pm.getApplicationInfo(pkg, 0)
                    pm.getApplicationLabel(info).toString()
                } catch (e: Exception) {
                    pkg
                }

                AppUsageDetails(
                    packageName = pkg,
                    applicationName = appName,
                    appUsage = usage.totalTimeInForeground / 1000,
                    date = isoFormatter.format(Date())
                )
            }
    }

    fun getLauncherApps(context: Context): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = context.packageManager

        return pm.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName }
            .toSet()
    }
}