package com.example.pitabmdmstudent.socket

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject


class SocketIOConnection @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    @ApplicationContext var context: Context,
) : IConnection {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    companion object {
        const val URL = "https://pi-os-backend.penpencil.co"
        const val DEVICE_PAIRING_STATUS_CHANGED = "device_pairing_status_changed"
        const val HMS_AUTH_TOKEN = "hms_auth_token"
        const val HMS_ROOM_CODE = "hms_room_code"
        const val HMS_ROOM_ID = "hms_room_id"
        const val APP_REQUEST_POSTED = "app_request_posted"
        const val APP_REQUEST_UPDATED = "app_request_updated"
        const val DEVICE_SCREENSHOT_REQUEST = "device_screenshot_request"
        const val DEVICE_SCREEN_SHARE_START = "device_screen_share_start"
        const val APP_BLOCK_RULE_UPDATED = "app_block_rule_updated"
        const val DEVICE_VIDEO_CALL_START = "device_video_call_start"
        const val DEVICE_VIDEO_CALL_END = "device_video_call_end"
        const val DEVICE_AUDIO_CALL_START = "device_audio_call_start"
        const val APP_MANAGEMENT_REMOVE_APP = "app_management_remove_app"
    }

    private var socket: Socket? = null

    private val _socketEvents = MutableSharedFlow<SocketEvent>(
        replay = 0,
        extraBufferCapacity = 1
    ) // sealed class of events

    val socketEvents: SharedFlow<SocketEvent> = _socketEvents

    private lateinit var authMap: MutableMap<String, String>

    private val onNewMessage = Emitter.Listener { args ->
        try {
            val raw = args.firstOrNull() ?: return@Listener
            val json = JSONObject(raw.toString())

            val event = json.optString("event")
            val messageObject = json.optJSONObject("message")

            if (messageObject == null) return@Listener;

            val packageName = messageObject.optString("packageName")
            val alwaysBlocked = messageObject.optBoolean("alwaysBlocked")
            val usageLimitSeconds = messageObject.optInt("usageLimitSeconds").toLong()

            Log.d("SocketTest", "Event: $event, Message: $messageObject, json: $json")
            println("Socket Event is -> $event json: $json")

            when (event) {
                APP_BLOCK_RULE_UPDATED -> {
                    scope.launch {
//                        localDatabase.updateIsBlocked(packageName, alwaysBlocked)
//                        localDatabase.updateTimeLimit(packageName, usageLimitSeconds)
//                        println(
//                            "Database data -> ${
//                                localDatabase.getAll()
//                                    .filter { it -> it.packageName.equals("co.penpencil.appstore") }
//                            }"
//                        )
                    }
                }

                DEVICE_SCREEN_SHARE_START -> {
//                    val message = json.optJSONObject("message") ?: JSONObject()
//                    val data = Gson().fromJson(message.toString(), HMSMessage::class.java)
//                    println("socket Data: $data")
//                    Log.d("CustomSSLog", "Pass0")
//                    val intent = Intent(
//                        context,
//                        Class.forName("co.penpencil.launcher.services.parentapp.LiveScreenShareService")
//                    )
//                    HMSTokenManager.setToken(data.token)
//                    ContextCompat.startForegroundService(context.applicationContext, intent)
                }

                DEVICE_AUDIO_CALL_START -> {
//                    val message = json.optJSONObject("message") ?: JSONObject()
//                    val data = Gson().fromJson(message.toString(), HMSMessage::class.java)
//                    println("socket Data: $data")
//                    val intent = Intent(
//                        context,
//                        Class.forName("co.penpencil.launcher.services.parentapp.SurroundingAudioService")
//                    )
//                    HMSTokenManager.setToken(data.token)
//                    ContextCompat.startForegroundService(context, intent)
                }

                DEVICE_VIDEO_CALL_START -> {
//                    val message = json.optJSONObject("message") ?: JSONObject()
//                    val data = Gson().fromJson(message.toString(), HMSMessage::class.java)
//                    println("socket Data: $data")
//                    _socketEvents.tryEmit(SocketEvent.HMSVideoCall(data.token))
//
//                    val serviceIntent = Intent(
//                        context,
//                        Class.forName("co.penpencil.launcher.services.parentapp.ParentVideoCallPopupService")
//                    )
//                    context.stopService(serviceIntent)
//
//                    val intent = Intent(
//                        context,
//                        Class.forName("co.penpencil.launcher.services.parentapp.ParentVideoCallPopupService")
//                    ).apply {
//                        putExtra(HMS_AUTH_TOKEN, data.token)
//                        putExtra(HMS_ROOM_CODE, data.roomCode)
//                        putExtra(HMS_ROOM_ID, data.roomId)
//                    }
//                    ContextCompat.startForegroundService(context, intent)
                }

                DEVICE_VIDEO_CALL_END -> {
//                    val serviceIntent = Intent(
//                        context,
//                        Class.forName("co.penpencil.launcher.services.parentapp.ParentVideoCallPopupService")
//                    )
//                    context.stopService(serviceIntent)
//
//                    _socketEvents.tryEmit(SocketEvent.VideoCallEnd(true))
                }

                DEVICE_SCREENSHOT_REQUEST -> {
//                    val raw = args.firstOrNull() ?: return@Listener
//                    val json = JSONObject(raw.toString())
//
//                    val event = json.optString("event")
//                    val messageObject = json.optJSONObject("message")
//                    val pairingId = messageObject.optString("pairingId")
//                    Log.d("CustomScreenLog", pairingId.toString())
//                    scope.launch {
//                        val screenshot = ScreenshotUtil.captureScreenBase64(context)
//                        Log.d("CustomScreenLog", screenshot.toString())
//                        val sendScreenshotRequest =
//                            SendScreenshotRequest(screenshot.toString(), pairingId)
//                        studentUseCase.sendScreenshot(sendScreenshotRequest).onSuccess {
//                            Log.d("CustomScreenLog", "API call successful")
//                        }.onFailure {
//                            Log.d("CustomScreenLog", "API call unsuccessful")
//                        }
//                    }
                }

                APP_MANAGEMENT_REMOVE_APP -> {
                    Log.d("CustomDeleteLog", "PackageManagerService reflection start")
                    val raw = args.firstOrNull() ?: return@Listener
                    val json = JSONObject(raw.toString())
                    val packageName = json.optJSONObject("message")?.optString("packageName")

                    if (packageName.isNullOrEmpty()) return@Listener

                    try {
                        // Get PackageManagerService
                        val serviceManagerClass = Class.forName("android.os.ServiceManager")
                        val getServiceMethod = serviceManagerClass.getDeclaredMethod("getService", String::class.java)
                        val packageManagerService = getServiceMethod.invoke(null, "package")

                        // Get IPackageManager interface
                        val iPackageManagerClass = Class.forName("android.content.pm.IPackageManager\$Stub")
                        val asInterfaceMethod = iPackageManagerClass.getDeclaredMethod("asInterface", Class.forName("android.os.IBinder"))
                        val iPackageManager = asInterfaceMethod.invoke(null, packageManagerService)

                        // Call deletePackageAsUser
                        val deleteMethod = iPackageManager.javaClass.getDeclaredMethod(
                            "deletePackageAsUser",
                            String::class.java,
                            Int::class.java,
                            Class.forName("android.content.pm.IPackageDeleteObserver"),
                            Int::class.java,
                            Int::class.java
                        )
                        deleteMethod.invoke(iPackageManager, packageName, -1, null, 0, 0)

                        Log.d("CustomDeleteLog", "PackageManagerService reflection successful")

                    } catch (e: Exception) {
                        Log.e("CustomDeleteLog", "PackageManagerService reflection failed: ${e.message}")
                    }
                }

                else -> {

                }
            }

        } catch (e: JSONException) {
            Log.d("SocketTest", "JSON Exception: $e")
            println("Socket Receive Exception : $e")
            Log.e(javaClass.name, e.message.orEmpty())
        }
    }

    override fun initializeCommunication() {
        try {
            scope.launch {
                val token = sharedPrefs.getString("socket_token", "AMBAvvunWpooIlRQkVK9cGw+srpLwesKwOdSYImLovs=")
                val userId = sharedPrefs.getString("user_id", "6921646bb7109a65974ec0e3")
                Log.d("SocketTest", "Socket Auth -> In initialize $token, $userId")
                println("Socket Auth -> In initialize $token, $userId")
                if (token != null && userId != null) {
                    authMap = mutableMapOf(
                        "token" to token,
                        "userId" to userId
                    )
                    Log.d("SocketTest", "Socket Auth -> $authMap")
                    println("Socket Auth -> $authMap")
                    socket = IO.socket(
                        "wss://pi-os-backend.penpencil.co",
                        IO.Options.builder()
                            .setPath("/ws")
                            .setTransports(arrayOf("websocket"))
                            .setForceNew(true)
                            .setAuth(authMap)
                            .setReconnection(true)
                            .setReconnectionDelay(1000)
                            .setReconnectionDelayMax(5000)
                            .build()
                    )
                    Log.d("SocketTest", "Socket : $socket")
                    println("Socket : $socket")
                    socket?.connect()
                    socket?.on(Socket.EVENT_CONNECT) {
                        Log.d("SocketTest", "Socket connected now")
                        println("Socket connected now")
                        _socketEvents.tryEmit(SocketEvent.Connected)
                        startListening()
                    }
                    socket?.on(Socket.EVENT_CONNECT_ERROR) {
                        Log.d("SocketTest", "Socket connection error: ${it.first()}")
                        println("Socket connection error: ${it.first()}")
                    }
                    socket?.on(Socket.EVENT_DISCONNECT) {
                        _socketEvents.tryEmit(SocketEvent.Disconnected)
                    }
                }
            }
        } catch (e: URISyntaxException) {
            Log.d("SocketTest", "Socket Exception: $e")
            println("Socket Exception: $e")
        }
    }

    override fun connect() {
        socket?.connect()
    }

    override fun disconnect() {
        socket?.disconnect()
    }


    // Start listening to multiple events
    private fun startListening() {
        Log.d("SocketTest", "Socket -> Inside startListening $socket")
        println("Socket -> Inside startListening $socket")
        if (socket?.connected() == true) {
            Log.d("SocketTest", "Socket is connected, starting to listen for events.")
            println("Socket is connected, starting to listen for events.")
            socket?.on(DEVICE_PAIRING_STATUS_CHANGED, onNewMessage)
            socket?.on("message", onNewMessage)
        } else {
            Log.d("SocketTest", "Socket is not connected.")
            println("Socket is not connected.")
        }
    }

    // Stop listening to multiple events
    fun stopListening() {
        if (socket?.connected() == true) {
            Log.d("SocketTest", "Socket is connected, stopping the listener.")
            println("Socket is connected, stopping the listener.")
            socket?.off(DEVICE_PAIRING_STATUS_CHANGED, onNewMessage)
        } else {
            Log.d("SocketTest", "Socket is not connected, no need to stop listening.")
            println("Socket is not connected, no need to stop listening.")
        }
    }

    override fun emit(event: String, data: Any) {
        // TODO("Not yet implemented")
    }

    override fun on(event: String, callback: (args: Array<Any>) -> Unit) {
        socket?.on(event, Emitter.Listener { args ->
            callback(args) // pass the event data to the callback function
        })
    }
}