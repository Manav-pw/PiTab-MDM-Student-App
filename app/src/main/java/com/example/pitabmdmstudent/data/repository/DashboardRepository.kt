package com.example.pitabmdmstudent.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import com.example.pitabmdmstudent.models.AppUsage
import com.example.pitabmdmstudent.services.MyAccessibilityService
import com.example.pitabmdmstudent.utils.AppUsageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val _todayUsageFlow = MutableStateFlow<Map<String, Long>>(emptyMap())
    val todayUsageFlow = _todayUsageFlow.asStateFlow()

    private val _weeklyUsageFlow = MutableStateFlow<Map<String, Map<String, Long>>>(emptyMap())
    val weeklyUsageFlow = _weeklyUsageFlow.asStateFlow()

    private val _totalTodayFlow = MutableStateFlow<Long>(0L)
    val totalTodayFlow = _totalTodayFlow.asStateFlow()

    private val _totalWeekFlow = MutableStateFlow<Long>(0L)
    val totalWeekFlow = _totalWeekFlow.asStateFlow()


    suspend fun loadTodayUsage() {
        val todayMap = AppUsageUtils.getTodayUsage(context)  // pkg → millis

        _todayUsageFlow.value = todayMap
        _totalTodayFlow.value = todayMap.values.sum()
    }

    suspend fun loadWeeklyUsage() {
        val weeklyUsage = AppUsageUtils.getWeeklyUsageByDay(context)

        _weeklyUsageFlow.value = weeklyUsage

        val totalWeek = weeklyUsage.values.flatMap { it.values }.sum()
        _totalWeekFlow.value = totalWeek
    }
}
