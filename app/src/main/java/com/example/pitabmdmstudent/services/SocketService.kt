package com.example.pitabmdmstudent.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.pitabmdmstudent.MainActivity
import com.example.pitabmdmstudent.data.repository.StudentRepository
import com.example.pitabmdmstudent.event.AppEventBus
import com.example.pitabmdmstudent.models.request.DeviceStateRequest
import com.example.pitabmdmstudent.models.request.VisibleApp
import com.example.pitabmdmstudent.socket.SocketEvent
import com.example.pitabmdmstudent.socket.SocketIOConnection
import com.example.pitabmdmstudent.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SocketService : Service() {

    @Inject lateinit var socket: SocketIOConnection

    @Inject lateinit var studentRepository: StudentRepository

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

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

        registerReceiver(
            BatteryReceiver(),
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        // Listen to socket events
        observeSocketEvents()
        observeSystemEvents()
        startIntervalTracking()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        job.cancel()
    }

    // ---------------------------------------
    // SOCKET LISTENERS
    // ---------------------------------------
    private fun observeSocketEvents() {
        serviceScope.launch {
            socket.socketEvents.collect { event ->
                Log.d("SocketService", "Socket Event: $event")
                when (event) {

                    is SocketEvent.Connected -> {
                        updateNotification("Connected")

                        val apps = AppUtils.getInstalledApps(applicationContext)
                        studentRepository.uploadInstalledApps(apps)
                        uploadDeviceState()
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

    private fun observeSystemEvents() {
        serviceScope.launch {
            AppEventBus.events.collect { event ->
                when (event) {
                    is AppEventBus.DeviceEvent.ForegroundAppChanged -> uploadDeviceState()
                    is AppEventBus.DeviceEvent.ChargingStateChanged -> uploadDeviceState()
                    is AppEventBus.DeviceEvent.AppStarted -> uploadDeviceState()
                    else -> {}
                }
            }
        }
    }

    private fun startIntervalTracking() {
        serviceScope.launch {
            while (true) {
                delay(6 * 60 * 1000)
                uploadDeviceState()
            }
        }
    }

    private suspend fun uploadDeviceState() {
        val visibleApps = getVisibleApps(applicationContext)
        val battery = AppUtils.getBatteryLevel(applicationContext)
        val charging = AppUtils.isCharging(applicationContext)

        val request = DeviceStateRequest(
            visibleApps = visibleApps,
            batteryReading = battery.toString(),
            batteryCharging = charging
        )

        studentRepository.updateDeviceState(request)
    }

    fun getVisibleApps(context: Context): List<VisibleApp> {
        val service = MyAccessibilityService.instance ?: return emptyList()

        val pkg = service.rootInActiveWindow?.packageName?.toString()
            ?: return emptyList()
        val label = getAppName(context, pkg)

        return listOf(
            VisibleApp(
                packageName = pkg,
                applicationName = label
            )
        )
    }

    private fun getAppName(context: Context, packageName: String) : String{
        val packageManager = context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }
}