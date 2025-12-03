package com.example.pitabmdmstudent.ui.activity

import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.pitabmdmstudent.MediaProjectionHolder
import com.example.pitabmdmstudent.services.SocketService
import com.example.pitabmdmstudent.ui.theme.PiTabMDMStudentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScreenCapturePermissionActivity : ComponentActivity() {
    private lateinit var projectionManager: MediaProjectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PiTabMDMStudentTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {}
            }
        }

        projectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val intent = projectionManager.createScreenCaptureIntent()
        startActivityForResult(intent, 999)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("MediaProjection","request worked")
        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {

            Log.d("MediaProjection","$requestCode $resultCode")
            MediaProjectionHolder.resultCode = resultCode
            MediaProjectionHolder.data = data

            // Notify the service that permission has been granted so it can
            // upgrade to a mediaProjection foreground service and obtain the
            // MediaProjection instance in a safe context.
            SocketService.onPermissionGranted(this)

            Log.d("ScreenPerm", "Permission granted, notifying SocketService")
        }

        finish()
    }
}