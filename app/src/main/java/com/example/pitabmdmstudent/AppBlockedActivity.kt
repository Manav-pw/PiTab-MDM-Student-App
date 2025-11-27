package com.example.pitabmdmstudent

import AppLimitWarningDialog
import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
                    MyAccessibilityService.instance?.performGlobalAction(GLOBAL_ACTION_HOME)
                    finish()
                }
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MyAccessibilityService.instance?.performGlobalAction(GLOBAL_ACTION_HOME)
                finish()
            }
        })
    }
}