package com.example.pitabmdmstudent.utils

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pitabmdmstudent.models.AppUsageDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object AppUsageUtils {
    fun getTodayStartTime(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getTodayUsage(context: Context): Map<String, Long> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return getUsageForTimeRange(usageStatsManager, startTime, endTime)
    }

    fun getWeeklyUsageByDay(context: Context): Map<String, Map<String, Long>> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val result = mutableMapOf<String, Map<String, Long>>()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0..6) {

            val startCal = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, i)
            }

            val endCal = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, i + 1)
            }

            val dayStart = startCal.timeInMillis
            val dayEnd = endCal.timeInMillis

            val dayUsage = getUsageForTimeRange(usageStatsManager, dayStart, dayEnd)

            val dateKey = dateFormat.format(Date(dayStart))
            result[dateKey] = dayUsage
        }

        return result
    }

    fun getIncrementalAppUsage(
        context: Context,
        usageStatsManager: UsageStatsManager,
        MDMpackageName: String,
        startTime: Long,
        endTime: Long
    ): List<AppUsageDetails> {
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val usageMap = mutableMapOf<String, Long>()
        val sessionMap = mutableMapOf<String, Long>()
        val activeApps = mutableSetOf<String>()

        val excludedPackages = setOf(
            "com.android.permissioncontroller",
            "com.android.systemui",
            "com.android.settings",
            MDMpackageName
        )

        // First pass: find apps that were already active at startTime
        // We need to check events BEFORE startTime to establish context
        val lookbackStart = maxOf(0, startTime - (24 * 60 * 60 * 1000))
        val priorEvents = usageStatsManager.queryEvents(lookbackStart, startTime)
        val lastEventMap = mutableMapOf<String, Int>()

        val tempEvent = UsageEvents.Event()
        while (priorEvents.hasNextEvent()) {
            priorEvents.getNextEvent(tempEvent)
            if (tempEvent.packageName in excludedPackages) continue
            lastEventMap[tempEvent.packageName] = tempEvent.eventType
        }

        // Explicitly check foreground/background states
        for ((pkg, lastType) in lastEventMap) {
            val isForeground = when (lastType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND,
                UsageEvents.Event.ACTIVITY_RESUMED -> true

                UsageEvents.Event.MOVE_TO_BACKGROUND,
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> false

                else -> false
            }

            if (isForeground) {
                activeApps.add(pkg)
                sessionMap[pkg] = startTime
                Log.d("IncrementalUsage", "$pkg was active at startTime")
            }
        }

        // Process actual events inside our range
        val rangeEvents = usageStatsManager.queryEvents(startTime, endTime)

        while (rangeEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            rangeEvents.getNextEvent(event)

            val pkg = event.packageName ?: continue
            if (pkg in excludedPackages) continue

            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND,
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    if (!activeApps.contains(pkg)) {
                        activeApps.add(pkg)
                        sessionMap[pkg] = event.timeStamp
                    }
                }

                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.MOVE_TO_BACKGROUND,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    if (activeApps.contains(pkg)) {
                        val start = sessionMap[pkg] ?: continue
                        val duration = event.timeStamp - start
                        if (duration > 0) {
                            usageMap[pkg] = (usageMap[pkg] ?: 0) + duration
                        }
                        activeApps.remove(pkg)
                        sessionMap.remove(pkg)
                    }
                }
            }
        }

        // For apps that were active at startTime, count from startTime (not their actual start)
        for (pkg in activeApps.toList()) {
            sessionMap[pkg] = startTime
        }

        // Second pass: process events in our target range
        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)

            val pkg = event.packageName ?: continue
            if (pkg in excludedPackages) continue

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    if (!activeApps.contains(pkg)) {
                        sessionMap[pkg] = event.timeStamp
                        activeApps.add(pkg)
                    }
                }

                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED,
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (activeApps.contains(pkg)) {
                        val sessionStart = sessionMap[pkg] ?: continue
                        val sessionDuration = event.timeStamp - sessionStart
                        if (sessionDuration > 0) {
                            usageMap[pkg] = (usageMap[pkg] ?: 0L) + sessionDuration
                        }
                        activeApps.remove(pkg)
                        sessionMap.remove(pkg)
                    }
                }
            }
        }

        // 3) Handle apps still active at endTime
        for ((pkg, start) in sessionMap) {
            val duration = endTime - start
            if (duration > 0) {
                usageMap[pkg] = (usageMap[pkg] ?: 0) + duration
            }
        }

        // Use the date when usage STARTED
        val calendar = Calendar.getInstance().apply {
            timeInMillis = startTime
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val dateString = dateFormat.format(Date(calendar.timeInMillis))

        Log.d("IncrementalUsage", "Usage date: $dateString (from startTime: ${Date(startTime)})")

        // Convert → AppUsageDetails
        val launcherApps = getLauncherApps(context)

        Log.d("LauncherApps","$launcherApps")

        return usageMap
            .filterKeys { it !in launcherApps }
            .map { (pkg, ms) ->
                AppUsageDetails(
                    packageName = pkg,
                    applicationName = AppUtils.getAppName(context, pkg),
                    appUsage = ms / 1000,
                    date = dateString
                )
            }
            .filter { it.appUsage > 0 }
    }

    fun getLauncherApps(context: Context): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = context.packageManager

        return pm.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName }
            .toSet()
    }

    private fun getUsageForTimeRange(
        usageStatsManager: UsageStatsManager,
        startTime: Long,
        endTime: Long
    ): Map<String, Long> {
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val usageMap = mutableMapOf<String, Long>()
        val sessionMap = mutableMapOf<String, Long>()
        val activeApps = mutableSetOf<String>()

        val excludedPackages = setOf(
            "com.android.permissioncontroller",
            "com.android.systemui",
            "com.android.settings",
            "co.penpencil.launcher"
        )

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)

            val pkg = event.packageName ?: continue
            if (pkg in excludedPackages) continue

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    if (!activeApps.contains(pkg)) {
                        sessionMap[pkg] = event.timeStamp
                        activeApps.add(pkg)
                    }
                }

                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED,
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (activeApps.contains(pkg)) {
                        val sessionStart = sessionMap[pkg] ?: continue
                        val durationMs = event.timeStamp - sessionStart
                        val durationSec = durationMs / 1000

                        if (durationSec > 0) {
                            usageMap[pkg] = (usageMap[pkg] ?: 0L) + durationSec
                        }
                        activeApps.remove(pkg)
                    }
                }
            }
        }

        for (pkg in activeApps) {
            val sessionStart = sessionMap[pkg] ?: continue
            val durationMs = endTime - sessionStart
            val durationSec = durationMs / 1000

            if (durationSec > 0) {
                usageMap[pkg] = (usageMap[pkg] ?: 0L) + durationSec
            }
        }

        return usageMap
    }
}