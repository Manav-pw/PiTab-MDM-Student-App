package com.example.pitabmdmstudent.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.pitabmdmstudent.MainActivity
import com.example.pitabmdmstudent.data.repository.StudentRepository
import com.example.pitabmdmstudent.socket.SocketEvent
import com.example.pitabmdmstudent.socket.SocketIOConnection
import com.example.pitabmdmstudent.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SocketService : Service() {

    @Inject lateinit var socket: SocketIOConnection

    @Inject lateinit var studentRepository: StudentRepository

    companion object {
        private const val NOTIFICATION_ID = 1987
        private const val CHANNEL_ID = "socket_channel"
        private const val CHANNEL_NAME = "MDM Socket Connection"

        fun start(context: Context) {
            Log.d("SocketTest", "Service STARTED")
            val intent = Intent(context, SocketService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, SocketService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Connecting…"))

        // Start socket
        socket.initializeCommunication()

        // Listen to socket events
        observeSocketEvents()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

    // ---------------------------------------
    // SOCKET LISTENERS
    // ---------------------------------------
    private fun observeSocketEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            socket.socketEvents.collect { event ->
                Log.d("SocketService", "Socket Event: $event")
                when (event) {

                    is SocketEvent.Connected -> {
                        updateNotification("Connected")

                        val apps = AppUtils.getInstalledApps(applicationContext)
                        studentRepository.uploadInstalledApps(apps)
                    }

                    is SocketEvent.Disconnected -> {
                        updateNotification("Disconnected")
                    }

                    is SocketEvent.HMSAudioCall -> TODO()
                    is SocketEvent.HMSScreenShare -> TODO()
                    is SocketEvent.HMSVideoCall -> TODO()
                    is SocketEvent.VideoCallEnd -> TODO()
                }
            }
        }
    }

    // ---------------------------------------
    // NOTIFICATION
    // ---------------------------------------
    private fun createNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("MDM Socket Service")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification(text))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

}