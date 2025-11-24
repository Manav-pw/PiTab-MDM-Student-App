package com.example.pitabmdmstudent.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {
    companion object {
        var lastForegroundPackage: String? = null
        var onEventCallback: ((String) -> Unit)? = null

        var foregroundAppCallback: ((String) -> Unit)? = null
    }

    private val blockedApps = mutableSetOf<String>()

    fun updateBlockedApps(list: List<String>) {
        blockedApps.clear()
        blockedApps.addAll(list)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val pkg = event.packageName?.toString() ?: return

        // --- Track foreground app ---
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            lastForegroundPackage = pkg
            onEventCallback?.invoke(pkg)
        }

        // --- Block app logic ---
        if (blockedApps.contains(pkg)) {
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    override fun onInterrupt() { }
}