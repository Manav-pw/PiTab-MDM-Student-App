package com.example.pitabmdmstudent.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pitabmdmstudent.services.SocketService
import com.example.pitabmdmstudent.ui.activity.HmsScreenActivity

class CallActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_ACCEPT = "com.example.ACTION_ACCEPT_HMS"
        const val ACTION_DECLINE = "com.example.ACTION_DECLINE_HMS"

        const val EXTRA_CALL_ID = "extra.call_id"
        const val EXTRA_TOKEN = "extra.token"
        const val EXTRA_PARENT = "extra.parent"

        const val EXTRA_CALL_TYPE = "extra.call_type"

        private const val INCOMING_NOTIFICATION_ID = 3001

    }

    override fun onReceive(context: Context, intent: Intent?) {
        var action = intent?.action ?: return
        when (action) {
            ACTION_ACCEPT -> {
                val callId = intent.getStringExtra(EXTRA_CALL_ID) ?: return
                val token = intent.getStringExtra(EXTRA_TOKEN) ?: return
                val parent = intent.getStringExtra(EXTRA_PARENT)

                // Open the HmsScreenActivity (bring app to foreground)
                val joinIntent = Intent(context, HmsScreenActivity::class.java).apply {
                    putExtra("callId", callId)
                    putExtra("token", token)
                    putExtra("parentName", parent)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context.startActivity(joinIntent)

                // Optionally: notify SocketService so it can emit ack to server
                val ackIntent = Intent(context, SocketService::class.java).apply {
                    action = SocketService.ACTION_HMS_ACCEPTED
                    putExtra("callId", callId)
                    putExtra("token", token)
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(ackIntent)
                } else {
                    context.startService(ackIntent)
                }

                // Cancel incoming notification (uses the main service NOTIFICATION_ID)
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(INCOMING_NOTIFICATION_ID)
            }

            ACTION_DECLINE -> {
                val callId = intent.getStringExtra(EXTRA_CALL_ID)
                // Inform SocketService (so it can notify backend) — optional
                val declineIntent = Intent(context, SocketService::class.java).apply {
                    action = SocketService.ACTION_HMS_DECLINED
                    putExtra("callId", callId)
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(declineIntent)
                } else {
                    context.startService(declineIntent)
                }

                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(INCOMING_NOTIFICATION_ID)
            }


        }
    }
}
