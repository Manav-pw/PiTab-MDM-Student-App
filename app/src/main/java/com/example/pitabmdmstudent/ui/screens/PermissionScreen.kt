package com.example.pitabmdmstudent.ui.screens

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pitabmdmstudent.navigation.Routes
import com.example.pitabmdmstudent.receivers.MyDeviceAdminReceiver
import okhttp3.Route

@Composable
fun PermissionScreen(navController: NavController) {

    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)).padding(25.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("We need permissions to continue:", color = Color.White, fontSize = 22.sp)

        Spacer(Modifier.height(20.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            PermissionButton("Enable Accessibility") {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }

            PermissionButton("Allow App Usage Access") {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }

            PermissionButton("Enable Device Admin") {
                val comp = ComponentName(context, MyDeviceAdminReceiver::class.java)
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, comp)
                context.startActivity(intent)
            }
        }



        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                val allGranted = isAccessibilityEnabled(context) &&
                        isUsageAccessGranted(context) &&
                        isDeviceAdminEnabled(context)
                if(allGranted){
                    navController.navigate(Routes.Dashboard.route)
                }
                else{
                    Toast.makeText(context,"Please give the required permissions to continue",
                        Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun PermissionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
    ) {
        Text(text)
    }
}
