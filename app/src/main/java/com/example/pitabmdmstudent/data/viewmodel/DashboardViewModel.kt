package com.example.pitabmdmstudent.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
}