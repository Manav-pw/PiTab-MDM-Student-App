package com.example.pitabmdmstudent.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pitabmdmstudent.event.AppEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BatteryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        CoroutineScope(Dispatchers.Default).launch {
            AppEventBus.emit(AppEventBus.DeviceEvent.ChargingStateChanged)
        }
    }
}