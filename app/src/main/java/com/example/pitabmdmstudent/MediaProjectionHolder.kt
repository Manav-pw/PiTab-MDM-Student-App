package com.example.pitabmdmstudent

import android.content.Intent
import android.media.projection.MediaProjection
import android.util.Log
import com.example.pitabmdmstudent.utils.ScreenshotUtil

object MediaProjectionHolder {
    var resultCode: Int = 0
    var data: Intent? = null
    var mediaProjection: MediaProjection? = null
    var callbackRegistered: Boolean = false

    fun isReady(): Boolean = mediaProjection != null

    fun reset() {
        ScreenshotUtil.cleanup() // Clean up VirtualDisplay first
        mediaProjection?.stop()
        mediaProjection = null
        data = null
        resultCode = 0
        callbackRegistered = false
        Log.d("Screenshott", "Reset complete")
    }
}