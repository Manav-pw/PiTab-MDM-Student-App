package com.example.pitabmdmstudent.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.pitabmdmstudent.ui.activity.AppBlockedActivity
import com.example.pitabmdmstudent.event.AppEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAccessibilityService : AccessibilityService() {
    companion object {
        var instance: MyAccessibilityService? = null
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
            Log.d("LimitTest", "Accesibility - Foreground app changed: $pkg")
            CoroutineScope(Dispatchers.IO).launch {
                AppEventBus.emit(AppEventBus.DeviceEvent.ForegroundAppChanged(pkg))
            }
        }
    }

    override fun onInterrupt() {}

    fun showBlockScreen(packageName: String, title: String, reason: String) {
        val intent = Intent(this, AppBlockedActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        intent.putExtra("appName", packageName)
        intent.putExtra("title", title)
        intent.putExtra("reason", reason)

        startActivity(intent)
    }
}