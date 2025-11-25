package com.example.pitabmdmstudent.models

data class AppUsageDetails(
    val packageName: String,
    val applicationName: String,
    val appUsage: Long,
    val date: String,
)