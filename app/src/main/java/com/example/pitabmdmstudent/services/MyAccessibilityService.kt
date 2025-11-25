package com.example.pitabmdmstudent.services

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.pitabmdmstudent.event.AppEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAccessibilityService : AccessibilityService() {
    companion object {
        var instance: MyAccessibilityService? = null
    }

    private val blockedApps = mutableSetOf<String>()

    fun updateBlockedApps(list: List<String>) {
        blockedApps.clear()
        blockedApps.addAll(list)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("SocketTest", "Service CREATED")
        SocketService.start(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            val pkg = event.packageName?.toString() ?: return
            CoroutineScope(Dispatchers.Default).launch {
                AppEventBus.emit(AppEventBus.DeviceEvent.ForegroundAppChanged(pkg))
            }
        }

//        // --- Block app logic ---
//        if (blockedApps.contains(pkg)) {
//            performGlobalAction(GLOBAL_ACTION_HOME)
//        }
    }

    override fun onInterrupt() { }
}