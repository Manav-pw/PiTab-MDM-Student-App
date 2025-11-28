package com.example.pitabmdmstudent

import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pitabmdmstudent.navigation.NavGraph
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
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {}
            }
        }

        projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val intent = projectionManager.createScreenCaptureIntent()
        startActivityForResult(intent, 999)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 999 && resultCode == RESULT_OK && data != null) {

            MediaProjectionHolder.resultCode = resultCode
            MediaProjectionHolder.data = data

            val mp = projectionManager.getMediaProjection(resultCode, data)
            MediaProjectionHolder.mediaProjection = mp

            if (!MediaProjectionHolder.callbackRegistered) {
                mp?.registerCallback(object : MediaProjection.Callback(){
                    override fun onStop() {
                        Log.d("ScreenPerm", "MediaProjection stopped by system")
                        MediaProjectionHolder.reset() // Use reset instead of manual cleanup
                    }
                }, Handler(Looper.getMainLooper()))

                MediaProjectionHolder.callbackRegistered = true
            }

            Log.d("ScreenPerm", "Permission granted, MediaProjection saved & callback registered!")
        }

        finish()
    }

}