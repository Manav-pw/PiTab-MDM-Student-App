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

        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {

            MediaProjectionHolder.resultCode = resultCode
            MediaProjectionHolder.data = data
            
            // Move MediaProjection creation to SocketService after it promotes to foreground type mediaProjection
            Log.d("ScreenPerm", "Permission granted, data saved. Asking Service to init projection.")
            SocketService.onPermissionGranted(this)
        }

        finish()
    }

}