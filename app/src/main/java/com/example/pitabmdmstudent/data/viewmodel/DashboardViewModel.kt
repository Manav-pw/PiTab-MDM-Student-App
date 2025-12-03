package com.example.pitabmdmstudent.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    val todayUsageFlow = repository.todayUsageFlow
    val weeklyUsageFlow = repository.weeklyUsageFlow
    val totalTodayFlow = repository.totalTodayFlow
    val totalWeekFlow = repository.totalWeekFlow

    fun loadTodayUsage() {
        viewModelScope.launch {
            repository.loadTodayUsage()
        }
    }

    fun loadWeeklyUsage() {
        viewModelScope.launch {
            repository.loadWeeklyUsage()
        }
    }

    fun getWeeklyTotalsPerDay(weeklyUsage: Map<String, Map<String, Long>>): Map<String, Long> {
        val orderedDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        return orderedDays.associateWith { day ->
            val usageForDate = weeklyUsage.entries.find { (date, _) ->
                val dayOfWeek = LocalDate.parse(date).dayOfWeek.getDisplayName(TextStyle.SHORT,
                    Locale.ENGLISH)
                dayOfWeek == day
            }?.value

            usageForDate?.values?.sum() ?: 0L
        }
    }
}