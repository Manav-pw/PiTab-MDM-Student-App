package com.example.pitabmdmstudent.services

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.content.edit
import com.example.pitabmdmstudent.MediaProjectionHolder
import com.example.pitabmdmstudent.appRestriction.AppBlockManager
import com.example.pitabmdmstudent.data.remote.repository.StudentRepository
import com.example.pitabmdmstudent.event.AppEventBus
import com.example.pitabmdmstudent.models.request.*
import com.example.pitabmdmstudent.receivers.BatteryReceiver
import com.example.pitabmdmstudent.socket.SocketEvent
import com.example.pitabmdmstudent.socket.SocketIOConnection
import com.example.pitabmdmstudent.ui.activity.MainActivity
import com.example.pitabmdmstudent.ui.activity.ScreenCapturePermissionActivity
import com.example.pitabmdmstudent.utils.AppUsageUtils
import com.example.pitabmdmstudent.utils.AppUtils
import com.example.pitabmdmstudent.utils.ScreenshotUtil
import com.example.pitabmdmstudent.utils.HmsUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SocketService : Service() {

    @Inject lateinit var socket: SocketIOConnection
    @Inject lateinit var studentRepository: StudentRepository
    @Inject lateinit var usageStatsManager: UsageStatsManager
    @Inject lateinit var sharedPrefs: SharedPreferences

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val todayUsageMap = mutableMapOf<String, Long>()
    private var sessionStartMs = 0L
    private var currentForegroundPackage: String? = null

    companion object {
        const val NOTIFICATION_ID = 1987
        const val CHANNEL_ID = "socket_channel"
        private const val CHANNEL_NAME = "MDM Socket Connection"
        private const val USAGE_PREF = "usage_stats_prefs"
        private const val KEY_LAST_SYNC = "last_sync_timestamp"

        const val ACTION_PERMISSION_GRANTED = "ACTION_PERMISSION_GRANTED"
        const val ACTION_HMS_ACCEPTED = "com.example.socket.ACTION_HMS_ACCEPTED"
        const val ACTION_HMS_DECLINED = "com.example.socket.ACTION_HMS_DECLINED"

        fun start(context: Context) {
            val intent = Intent(context, SocketService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun onPermissionGranted(context: Context) {
            val intent = Intent(context, SocketService::class.java).apply {
                action = ACTION_PERMISSION_GRANTED
            }
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

        // START FGS WITHOUT mediaProjection
        startForegroundServiceWithNotification("Connecting…", includeMediaProjection = false)

        serviceScope.launchSafely { initializeSocket() }
        getMediaProjection()
        registerReceiver(BatteryReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        AppBlockManager.initialize(applicationContext)

        observeSocketEvents()
        observeSystemEvents()
        startUsageStatsUploader()
        startAppLimitMonitor()
    }

    private fun startForegroundServiceWithNotification(
        text: String,
        includeMediaProjection: Boolean
    ) {
        val notification = createNotification(text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = if (includeMediaProjection)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            else ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            startForeground(NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_PERMISSION_GRANTED -> {
                    // Upgrade FGS to include MediaProjection now that permission is granted
                    startForegroundServiceWithNotification("Screen capture ready", includeMediaProjection = true)
                    setupMediaProjectionFromHolder()
                }
                ACTION_HMS_ACCEPTED -> handleHmsAction(intent.getStringExtra("callId"), accepted = true)
                ACTION_HMS_DECLINED -> handleHmsAction(intent.getStringExtra("callId"), accepted = false)
            }
        }
        return START_STICKY
    }

    private fun handleHmsAction(callId: String?, accepted: Boolean) {
        if (callId == null) return
        serviceScope.launchSafely {
            try {
                if (accepted) socket.emitHmsAccepted(callId)
                else socket.emitHmsDeclined(callId)
            } catch (e: Exception) {
                Log.e("SocketService", "HMS action failed", e)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        serviceJob.cancel()
    }

    private fun getMediaProjection() {
        if (MediaProjectionHolder.mediaProjection == null) {
            val intent = Intent(this, ScreenCapturePermissionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            ScreenshotUtil.setupScreenCapture(applicationContext)
        }
    }

    private fun setupMediaProjectionFromHolder() {
        MediaProjectionHolder.mediaProjection?.let {
            ScreenshotUtil.setupScreenCapture(applicationContext)
            return
        }

        val data = MediaProjectionHolder.data ?: return
        val resultCode = MediaProjectionHolder.resultCode

        val projectionManager = getSystemService(MediaProjectionManager::class.java) ?: return
        val mp: MediaProjection? = try {
            projectionManager.getMediaProjection(resultCode, data)
        } catch (e: SecurityException) {
            Log.e("MediaProjection", "Failed to get projection", e)
            null
        }
        MediaProjectionHolder.mediaProjection = mp

        mp?.let {
            if (!MediaProjectionHolder.callbackRegistered) {
                it.registerCallback(object : MediaProjection.Callback() {
                    override fun onStop() {
                        MediaProjectionHolder.reset()
                    }
                }, Handler(Looper.getMainLooper()))
                MediaProjectionHolder.callbackRegistered = true
            }
            ScreenshotUtil.setupScreenCapture(applicationContext)
        }
    }

    // -----------------------
    // SOCKET INITIALIZATION
    // -----------------------
    private suspend fun initializeSocket() {
        try {
            socket.initializeCommunication()
        } catch (e: Exception) {
            Log.e("SocketService", "Socket initialization failed", e)
        }
    }

    private fun observeSocketEvents() {
        serviceScope.launchSafely {
            socket.socketEvents.collect { event ->
                Log.d("SocketService", "Socket Event: $event")
                when (event) {
                    is SocketEvent.Connected -> {
                        updateNotification("Connected")
                        studentRepository.uploadInstalledApps(AppUtils.getAllInstalledApps(applicationContext))
                        uploadDeviceState()
                    }
                    is SocketEvent.Disconnected -> updateNotification("Disconnected")
                    is SocketEvent.ScreenshotRequest -> handleScreenshot(event.pairingId)
                    is SocketEvent.HMSScreenShare -> {
                        serviceScope.launchSafely {
                            HmsUtils.showIncomingCallNotification(applicationContext, event.hmsData, "screen_share")
                        }
                    }
                    is SocketEvent.HMSVideoCall -> {
                        serviceScope.launchSafely {
                            HmsUtils.showIncomingCallNotification(applicationContext, event.hmsData, "video_call")
                        }
                    }
                    is SocketEvent.HMSAudioCall -> {
                        serviceScope.launchSafely {
                            HmsUtils.showIncomingCallNotification(applicationContext, event.hmsData, "audio_call")
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleScreenshot(pairingId: String) {
        serviceScope.launchSafely {
            val base64 = ScreenshotUtil.takeScreenshot(applicationContext) ?: run {
                Log.e("Screenshot", "Failed to capture screen")
                return@launchSafely
            }
            val request = SendScreenshotRequest(pairingId = pairingId, screenshotBase64 = base64)
            val success = studentRepository.sendScreenshot(pairingId, request)
            Log.d("ScreenshotTest", "API success = $success")
        }
    }

    // -----------------------
    // NOTIFICATIONS
    // -----------------------
    private fun createNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
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
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    // -----------------------
    // SYSTEM EVENTS
    // -----------------------
    private fun observeSystemEvents() {
        serviceScope.launchSafely {
            AppEventBus.events.collect { event ->
                when (event) {
                    is AppEventBus.DeviceEvent.ForegroundAppChanged -> {
                        val now = System.currentTimeMillis()
                        currentForegroundPackage?.let { previous ->
                            todayUsageMap[previous] = (todayUsageMap[previous] ?: 0L) + (now - sessionStartMs)
                        }
                        currentForegroundPackage = event.packageName
                        sessionStartMs = now
                        currentForegroundPackage?.let { AppBlockManager.checkAndEnforceBlocking(it) }
                        uploadDeviceState()
                    }
                    is AppEventBus.DeviceEvent.ChargingStateChanged,
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
        val request = DeviceStateRequest(visibleApps, battery.toString(), charging)
        try {
            studentRepository.updateDeviceState(request)
        } catch (e: Exception) {
            Log.e("SocketService", "Device state upload failed", e)
        }
    }

    fun getVisibleApps(context: Context): List<VisibleApp> {
        val service = MyAccessibilityService.instance ?: return emptyList()
        val pkg = service.rootInActiveWindow?.packageName?.toString() ?: return emptyList()
        val label = AppUtils.getAppName(context, pkg)
        return listOf(VisibleApp(pkg, label))
    }

    // -----------------------
    // USAGE STATS
    // -----------------------
    private fun startUsageStatsUploader() {
        serviceScope.launchSafely {
            while (isActive) {
                uploadIncrementalAppUsage()
                delay(60_000)
            }
        }
    }

    private suspend fun uploadIncrementalAppUsage() {
        val currentTime = System.currentTimeMillis()
        val lastSync = sharedPrefs.getLong(KEY_LAST_SYNC, AppUsageUtils.getTodayStartTime())
        val usage = AppUsageUtils.getIncrementalAppUsage(
            applicationContext, usageStatsManager, packageName, lastSync, currentTime
        )
        if (usage.isEmpty()) {
            sharedPrefs.edit { putLong(KEY_LAST_SYNC, currentTime) }
            return
        }
        val request = AppUsageStatsRequest(usage)
        val success = try { studentRepository.postAppUsageStats(request) } catch (e: Exception) { false }
        if (success) sharedPrefs.edit { putLong(KEY_LAST_SYNC, currentTime) }
    }

    private fun startAppLimitMonitor() {
        serviceScope.launchSafely {
            while (isActive) {
                currentForegroundPackage?.let { pkg ->
                    val now = System.currentTimeMillis()
                    todayUsageMap[pkg] = (todayUsageMap[pkg] ?: 0L) + (now - sessionStartMs)
                    sessionStartMs = now

                    val usedSeconds = (todayUsageMap[pkg] ?: 0L) / 1000
                    val limitSeconds = AppBlockManager.getRule(pkg)?.usageLimitSeconds ?: 0
                    if (limitSeconds > 0 && usedSeconds >= limitSeconds) {
                        MyAccessibilityService.instance?.showBlockScreen(
                            pkg, "App blocked", "You have reached today's time limit for this app."
                        )
                    }
                }
                delay(5000)
            }
        }
    }

    // -----------------------
    // SAFE LAUNCH UTILITY
    // -----------------------
    private fun CoroutineScope.launchSafely(block: suspend CoroutineScope.() -> Unit) =
        this.launch {
            try {
                block()
            } catch (e: Exception) {
                Log.e("SocketService", "Coroutine crash", e)
            }
        }
}
