package com.example.pitabmdmstudent.models.request

data class CallLogRequest(
    val callDetails: List<CallLogEntry>
)

data class CallLogEntry(
    val name: String?,
    val number: Long,
    val callType: String,
    val duration: Long,
    val time: String
)