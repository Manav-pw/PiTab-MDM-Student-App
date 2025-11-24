package com.example.pitabmdmstudent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitabmdmstudent.models.AppUsage
import com.example.pitabmdmstudent.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    val appUsageFlow: StateFlow<List<AppUsage>> = repository.usageFlow

    fun loadWeeklyUsage() {
        viewModelScope.launch {
            repository.loadWeeklyUsage()
        }
    }
}