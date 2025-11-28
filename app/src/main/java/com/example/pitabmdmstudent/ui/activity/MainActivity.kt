package com.example.pitabmdmstudent.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.pitabmdmstudent.navigation.NavGraph
import com.example.pitabmdmstudent.ui.theme.PiTabMDMStudentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1000)
            }
        }

//        AppEventBus.emit(AppEventBus.DeviceEvent.ForegroundAppChanged)

        setContent {
            PiTabMDMStudentTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}