package com.example.pitabmdmstudent.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AppEventBus {
    private val _events = MutableSharedFlow<DeviceEvent>(extraBufferCapacity = 2)
    val events = _events.asSharedFlow()

    suspend fun emit(event: DeviceEvent) {
        _events.emit(event)
    }

    sealed class DeviceEvent {
        data class ForegroundAppChanged(val packageName: String) : DeviceEvent()
        object ChargingStateChanged : DeviceEvent()
        object AppStarted : DeviceEvent()
    }
}
