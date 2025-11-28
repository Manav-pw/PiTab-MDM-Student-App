package com.example.pitabmdmstudent.ui.activity

import AppLimitWarningDialog
import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.example.pitabmdmstudent.R
import com.example.pitabmdmstudent.services.MyAccessibilityService

class AppBlockedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appName = intent.getStringExtra("appName") ?: ""
        val reason = intent.getStringExtra("reason") ?: "blocked"
        val title = intent.getStringExtra("title") ?: "This app is blocked"

        setContent {
            AppLimitWarningDialog(
                iconRes = R.drawable.ic_hour_glass,
                title = title,
                message = reason,
                onDismiss = {
                    MyAccessibilityService.Companion.instance?.performGlobalAction(
                        AccessibilityService.GLOBAL_ACTION_HOME
                    )
                    finish()
                }
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MyAccessibilityService.Companion.instance?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
                finish()
            }
        })
    }
}