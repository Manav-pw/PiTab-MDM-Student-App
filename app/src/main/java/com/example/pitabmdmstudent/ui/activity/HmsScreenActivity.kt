package com.example.pitabmdmstudent.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.example.pitabmdmstudent.data.viewmodel.HmsViewModel
import com.example.pitabmdmstudent.ui.components.hms.HmsScreen
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class HmsScreenActivity : ComponentActivity() {

    private val viewModel: HmsViewModel by viewModels()

    // Request permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false

        if (cameraGranted && audioGranted) {
            initSession()   // ✅ NOW setContent happens AFTER permissions
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    private fun initSession() {

        val token = intent.getStringExtra("token")
        val parentName = intent.getStringExtra("parentName") ?: "Student"
        val callType = intent.getStringExtra("callType") ?: "screen_share"

        viewModel.setCallType(callType)

        if (token != null) {
            viewModel.joinRoom(
                userName = parentName,
                authToken = token
            )
        }

        // ✅ setContent AFTER ViewModel is ready
        setContent {

            // 🚀 AUTO SCREENSHARE COLLECTOR FIX
            val shouldStart = viewModel.shouldStartScreenShare.collectAsState()

            LaunchedEffect(shouldStart.value) {
                if (shouldStart.value) {
                    requestScreenShare()
                }
            }

            HmsScreen(
                viewModel = viewModel,
                onEndCall = {
                    viewModel.leaveRoom()
                    finish()
                }
            )
        }
    }

    private val screenShareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            viewModel.startScreenShare(result.data!!)
        }
    }

    fun requestScreenShare() {
        val mediaProjectionManager = getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager

        val intent = mediaProjectionManager.createScreenCaptureIntent()
        screenShareLauncher.launch(intent)
    }
}
