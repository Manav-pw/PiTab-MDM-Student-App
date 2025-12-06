package com.example.pitabmdmstudent.utils

import android.Manifest
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import com.example.pitabmdmstudent.models.HmsIncomingEvent
import com.example.pitabmdmstudent.receivers.CallActionReceiver
import com.example.pitabmdmstudent.services.SocketService
import com.example.pitabmdmstudent.ui.activity.IncomingCallActivity

object HmsUtils {

    // Use the same channel id as your SocketService (CHANNEL_ID). If you change, keep in sync.
    private const val CALL_CHANNEL_ID = "incoming_call_channel"
    private const val INCOMING_NOTIFICATION_ID = 3001 // local id for incoming call

    @RequiresPermission(Manifest.permission.USE_FULL_SCREEN_INTENT)
    fun showIncomingCallNotification(context: Context, hms: HmsIncomingEvent, callType: String) {
        // Ensure channel exists (low-cost check)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = nm.getNotificationChannel(CALL_CHANNEL_ID)

            if (channel == null) {
                val newChannel = NotificationChannel(
                    CALL_CHANNEL_ID,
                    if(callType == "screen_share") "Incoming Screen Share Request" else if(callType == "video_call") "Incoming Video Call" else "Incoming Audio Call",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = if(callType == "screen_share") "Incoming Screen Share Request" else if(callType == "video_call") "Incoming Video Call" else "Incoming Audio Call"

                    enableVibration(true)
                    enableLights(true)

                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()

                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                        audioAttributes
                    )
                }

                nm.createNotificationChannel(newChannel)
            }
        }

        // Accept / Decline intents
        val acceptIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_ACCEPT
            putExtra(CallActionReceiver.EXTRA_CALL_ID, hms.callId)
            putExtra(CallActionReceiver.EXTRA_TOKEN, hms.token)
            putExtra(CallActionReceiver.EXTRA_PARENT, hms.parentName)
            putExtra(CallActionReceiver.EXTRA_CALL_TYPE, callType)
        }
        val declineIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = CallActionReceiver.ACTION_DECLINE
            putExtra(CallActionReceiver.EXTRA_CALL_ID, hms.callId)
        }

        val acceptPI = PendingIntent.getBroadcast(
            context, 1001, acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val declinePI = PendingIntent.getBroadcast(
            context, 1002, declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(context, IncomingCallActivity::class.java).apply {
            putExtra("callId", hms.callId)
            putExtra("token", hms.token)
            putExtra("parentName", hms.parentName)
            putExtra("callType", callType)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPI = PendingIntent.getActivity(
            context, 1003, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CALL_CHANNEL_ID)
                .setContentTitle(if(callType == "screen_share") "Incoming Screen Share Request" else if(callType == "video_call") "Incoming Video Call" else "Incoming Audio Call")
                .setContentText("${hms.parentName} ${if(callType == "screen_share") " wants you to share your screen" else if(callType == "video_call") " wants to hop on a Video Call" else " wants to hop on an Audio Call"}")
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setCategory(Notification.CATEGORY_CALL)
                .setAutoCancel(true)
                .addAction(
                    Notification.Action.Builder(
                        Icon.createWithResource(context, R.drawable.ic_menu_call),
                        "Accept",
                        acceptPI
                    ).build()
                )
                .addAction(
                    Notification.Action.Builder(
                        Icon.createWithResource(context, R.drawable.ic_menu_close_clear_cancel),
                        "Decline",
                        declinePI
                    ).build()
                )
                .setFullScreenIntent(fullScreenPI, true)
                .build()
        } else {
            Notification.Builder(context)
                .setContentTitle(if(callType == "screen_share") "Incoming Screen Share Request" else if(callType == "video_call") "Incoming Video Call" else "Incoming Audio Call")
                .setContentText("${hms.parentName} ${if(callType == "screen_share") " wants you to share your screen" else if(callType == "video_call") " wants to hop on a Video Call" else " wants to hop on an Audio Call"}")
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_menu_call, "Accept", acceptPI)
                .addAction(R.drawable.ic_menu_close_clear_cancel, "Decline", declinePI)
                .build()
        }

        try {
            NotificationManagerCompat.from(context).notify(INCOMING_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Fallback: show a normal notification without full-screen intent
            val fallbackNotification = Notification.Builder(context)
                .setContentTitle(if(callType == "screen_share") "Incoming Screen Share Request" else if(callType == "video_call") "Incoming Video Call" else "Incoming Audio Call")
                .setContentText("${hms.parentName} ${if(callType == "screen_share") " wants you to share your screen" else if(callType == "video_call") " wants to hop on a Video Call" else " wants to hop on an Audio Call"}")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
            NotificationManagerCompat.from(context).notify(INCOMING_NOTIFICATION_ID, fallbackNotification)
        }    }
}