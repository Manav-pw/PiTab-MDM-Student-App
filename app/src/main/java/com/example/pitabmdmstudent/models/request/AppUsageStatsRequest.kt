package com.example.pitabmdmstudent.models.request

import com.example.pitabmdmstudent.models.AppUsageDetails

data class AppUsageStatsRequest(
    val usageDetails: List<AppUsageDetails>
)