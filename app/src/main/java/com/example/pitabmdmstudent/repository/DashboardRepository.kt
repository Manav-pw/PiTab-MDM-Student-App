package com.example.pitabmdmstudent.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import com.example.pitabmdmstudent.models.AppUsage
import com.example.pitabmdmstudent.services.MyAccessibilityService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageStatsManager: UsageStatsManager
) {

    private val _usageFlow = MutableStateFlow<List<AppUsage>>(emptyList())
    val usageFlow = _usageFlow.asStateFlow()

    private val usageMap = mutableMapOf<String, Long>()  // pkg → total time millis

    init {
        // Receive callbacks from AccessibilityService
//        MyAccessibilityService.foregroundAppCallback = { pkg ->
//            updateRealtimeUsage(pkg)
//        }
    }

    private var lastPackage = ""
    private var lastTimestamp = System.currentTimeMillis()

    // Called whenever foreground app changes
    private fun updateRealtimeUsage(pkg: String) {
        val now = System.currentTimeMillis()

        if (lastPackage.isNotEmpty()) {
            val delta = now - lastTimestamp
            usageMap[lastPackage] = (usageMap[lastPackage] ?: 0L) + delta
        }

        lastTimestamp = now
        lastPackage = pkg

        emitUsage()
    }

    suspend fun loadWeeklyUsage() {
        val end = System.currentTimeMillis()
        val start = end - 7 * 24 * 60 * 60 * 1000L

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end
        )

        stats.forEach {
            val minutes = (it.totalTimeInForeground / 60000).toInt()
            if (minutes > 0) {
                usageMap[it.packageName] = it.totalTimeInForeground
            }
        }

        emitUsage()
    }

    private fun emitUsage() {
        val pm = context.packageManager

        val apps = usageMap.map { (pkg, timeMs) ->
            val minutes = (timeMs / 60000).toInt()

            val appName = try {
                pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
            } catch (e: Exception) {
                pkg
            }

            AppUsage(appName, minutes)
        }.sortedByDescending { it.usageMinutes }

        _usageFlow.value = apps
    }
}
