package com.example.pitabmdmstudent.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.pitabmdmstudent.services.SocketService
import com.example.pitabmdmstudent.ui.screens.IncomingCallScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomingCallActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep on lock screen + turn screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val callId = intent.getStringExtra("callId")
        val token = intent.getStringExtra("token")
        val parentName = intent.getStringExtra("parentName") ?: "Unknown"
        val callType = intent.getStringExtra("callType") ?: "video_call"

        setContent {
            IncomingCallScreen(
                callerName = parentName,
                callType = callType,
                onAccept = {
                    // start HmsScreenActivity
                    val joinIntent = Intent(this, HmsScreenActivity::class.java).apply {
                        putExtra("callId", callId)
                        putExtra("token", token)
                        putExtra("parentName", parentName)
                        putExtra("callType", callType)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    startActivity(joinIntent)

                    // inform SocketService to emit accepted
                    val ackIntent = Intent(this, SocketService::class.java).apply {
                        action = SocketService.ACTION_HMS_ACCEPTED
                        putExtra("callId", callId)
                        putExtra("token", token)
                        putExtra("callType", callType)

                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(ackIntent)
                    else startService(ackIntent)

                    finish()
                },
                onReject = {
                    // inform SocketService to emit declined
                    val declineIntent = Intent(this, SocketService::class.java).apply {
                        action = SocketService.ACTION_HMS_DECLINED
                        putExtra("callId", callId)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(declineIntent)
                    else startService(declineIntent)
                    finish()
                }
            )
        }
    }
}
