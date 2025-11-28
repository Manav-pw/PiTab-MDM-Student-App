package com.example.pitabmdmstudent.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.edit
import com.example.pitabmdmstudent.ui.activity.MainActivity
import com.example.pitabmdmstudent.MediaProjectionHolder
import com.example.pitabmdmstudent.ui.activity.ScreenCapturePermissionActivity
import com.example.pitabmdmstudent.appRestriction.AppBlockManager
import com.example.pitabmdmstudent.data.remote.repository.StudentRepository
import com.example.pitabmdmstudent.event.AppEventBus
import com.example.pitabmdmstudent.models.request.AppUsageStatsRequest
import com.example.pitabmdmstudent.models.request.DeviceStateRequest
import com.example.pitabmdmstudent.models.request.SendScreenshotRequest
import com.example.pitabmdmstudent.models.request.VisibleApp
import com.example.pitabmdmstudent.receivers.BatteryReceiver
import com.example.pitabmdmstudent.socket.SocketEvent
import com.example.pitabmdmstudent.socket.SocketIOConnection
import com.example.pitabmdmstudent.utils.AppUsageUtils
import com.example.pitabmdmstudent.utils.AppUtils
import com.example.pitabmdmstudent.utils.ScreenshotUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SocketService : Service() {
    @Inject
    lateinit var socket: SocketIOConnection

    @Inject
    lateinit var studentRepository: StudentRepository

    @Inject
    lateinit var usageStatsManager: UsageStatsManager

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    private val todayUsageMap = mutableMapOf<String, Long>()
    private var sessionStartMs = 0L
    private var currentForegroundPackage: String? = null

    companion object {
        private const val NOTIFICATION_ID = 1987
        private const val CHANNEL_ID = "socket_channel"
        private const val CHANNEL_NAME = "MDM Socket Connection"

        private const val USAGE_PREF = "usage_stats_prefs"
        private const val KEY_LAST_SYNC = "last_sync_timestamp"

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

        sharedPrefs = getSharedPreferences(USAGE_PREF, Context.MODE_PRIVATE)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Connecting…"))

        socket.initializeCommunication()

        if (MediaProjectionHolder.mediaProjection == null) {
            val intent = Intent(this, ScreenCapturePermissionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            ScreenshotUtil.setupScreenCapture(applicationContext)
        }

        registerReceiver(
            BatteryReceiver(),
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        AppBlockManager.initialize(applicationContext)

        observeSocketEvents()
        observeSystemEvents()
        startUsageStatsUploader()
        startAppLimitMonitor()
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

                    is SocketEvent.ScreenshotRequest -> {
                        Log.d("ScreenshotTest", "Received screenshot request")
                        if (MediaProjectionHolder.isReady()) {
                            ScreenshotUtil.setupScreenCapture(applicationContext)
                        }

                        handleScreenshot(event.pairingId)
                    }

                    is SocketEvent.HMSAudioCall -> TODO()
                    is SocketEvent.HMSScreenShare -> TODO()
                    is SocketEvent.HMSVideoCall -> TODO()
                    is SocketEvent.VideoCallEnd -> TODO()

                }
            }
        }
    }

    private fun handleScreenshot(pairingId: String) {
        serviceScope.launch {
            try {
                val base64 = ScreenshotUtil.takeScreenshot(applicationContext)

                if (base64 == null) {
                    Log.e("Screenshot", "Failed to capture screen")
                    return@launch
                }

                val request = SendScreenshotRequest(
                    screenshotBase64 = base64,
                    pairingId = pairingId
                )

                val success = studentRepository.sendScreenshot(
                    pairingId = pairingId,
                    sendScreenshotRequest = request
                )

                Log.d("ScreenshotTest", "API success = $success")

            } catch (e: Exception) {
                Log.e("Screenshot", "Error while sending screenshot", e)
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
                    is AppEventBus.DeviceEvent.ForegroundAppChanged -> {
                        val now = System.currentTimeMillis()
                        currentForegroundPackage?.let { previousPkg ->
                            todayUsageMap[previousPkg] =
                                (todayUsageMap[previousPkg] ?: 0) + (now - sessionStartMs)
                            Log.d("LimitTest", "usage map updated: $todayUsageMap")
                        }
                        currentForegroundPackage = event.packageName
                        sessionStartMs = now
                        currentForegroundPackage?.let { AppBlockManager.checkAndEnforceBlocking(it) }
                        uploadDeviceState()
                    }

                    is AppEventBus.DeviceEvent.ChargingStateChanged -> uploadDeviceState()
                    // TODO: IMPLEMENT APPSTARTED IF NEEDED
                    is AppEventBus.DeviceEvent.AppStarted -> uploadDeviceState()
                    else -> {}
                }
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
        val label = AppUtils.getAppName(context, pkg)

        return listOf(
            VisibleApp(
                packageName = pkg,
                applicationName = label
            )
        )
    }

    private fun startUsageStatsUploader() {
        serviceScope.launch {
            while (true) {
                try {
                    uploadIncrementalAppUsage()
                } catch (e: Exception) {
                    Log.e("UsageUploader", "Error uploading usage", e)
                }
//                delay(5 * 60 * 1000) // every 5 minutes
                delay(60 * 1000)
            }
        }
    }

    private suspend fun uploadIncrementalAppUsage() {
        val currentTime = System.currentTimeMillis()
        val lastSyncTime = sharedPrefs.getLong(KEY_LAST_SYNC, AppUsageUtils.getTodayStartTime())

        Log.d("UsageUploader", "Syncing from $lastSyncTime to $currentTime")

        // Get incremental usage since last sync
        val incrementalUsage = AppUsageUtils.getIncrementalAppUsage(
            applicationContext,
            usageStatsManager,
            applicationContext.packageName,
            lastSyncTime,
            currentTime
        )

        if (incrementalUsage.isEmpty()) {
            Log.d("UsageUploader", "No new usage data since last sync")
            // Still update sync time even if no data
            sharedPrefs.edit { putLong(KEY_LAST_SYNC, currentTime) }
            return
        }

        Log.d("UsageUploader", "Incremental usage: $incrementalUsage")

        val request = AppUsageStatsRequest(incrementalUsage)

        val success = studentRepository.postAppUsageStats(request)

        if (success) {
            sharedPrefs.edit { putLong(KEY_LAST_SYNC, currentTime) }
            Log.d("UsageUploader", "Successfully uploaded ${incrementalUsage.size} app records")
        } else {
            Log.e("UsageUploader", "Failed to upload usage stats - will retry next sync")
        }
    }

    private fun startAppLimitMonitor() {
        serviceScope.launch {
            while (true) {
                val pkg = currentForegroundPackage
                Log.d("LimitTest", "CurrPkg in Monitor: $pkg")
                if (pkg != null) {
                    val now = System.currentTimeMillis()

                    todayUsageMap[pkg] =
                        (todayUsageMap[pkg] ?: 0L) + (now - sessionStartMs)

                    sessionStartMs = now

                    Log.d("LimitTest", "Checking limit")
                    val usedSeconds = (todayUsageMap[pkg] ?: 0L) / 1000
                    val rule = AppBlockManager.getRule(pkg)
                    val limitSeconds = rule?.usageLimitSeconds ?: 0

                    Log.d("LimitTest", "usageSeconds: $usedSeconds, limitSeconds: $limitSeconds")

                    if (limitSeconds > 0 && usedSeconds >= limitSeconds) {
                        Log.d("LimitTest", "$pkg exceeded limit $usedSeconds / $limitSeconds")

                        MyAccessibilityService.instance?.showBlockScreen(
                            pkg,
                            "App blocked",
                            "You have reached today's time limit for this app."
                        )
                    }
                }
                delay(5000)
            }
        }
    }
}