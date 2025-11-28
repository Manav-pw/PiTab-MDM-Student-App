package com.example.pitabmdmstudent.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.util.Base64
import android.util.Log
import com.example.pitabmdmstudent.MediaProjectionHolder
import java.io.ByteArrayOutputStream
import androidx.core.graphics.createBitmap

object ScreenshotUtil {
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var isSetup = false

    fun setupScreenCapture(context: Context): Boolean {
        if (isSetup) {
            return true
        }

        val mp = MediaProjectionHolder.mediaProjection
        if (mp == null) {
            Log.e("ScreenshotUtils", "MediaProjection is not ready")
            return false
        }

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        try {
            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

            virtualDisplay = mp.createVirtualDisplay(
                "screencap",
                width,
                height,
                density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader!!.surface,
                null,
                null
            )

            isSetup = true
            return true
        } catch (e: Exception) {
            Log.e("ScreenshotUtils", "Error setting up screen capture", e)
            cleanup()
            return false
        }
    }

    fun takeScreenshot(context: Context): String? {

        if (!isSetup && !setupScreenCapture(context)) {
            return null
        }

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        Thread.sleep(200)

        val image = imageReader?.acquireLatestImage()
        if (image == null) {
            Log.e("ScreenshotUtils", "No image acquired from ImageReader")
            return null
        }

        try {
            val plane = image.planes[0]
            val buffer = plane.buffer
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val rowPadding = rowStride - pixelStride * width

            val bmp = createBitmap(width + rowPadding / pixelStride, height)
            bmp.copyPixelsFromBuffer(buffer)

            val finalBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height)
            bmp.recycle()

            val base64 = bitmapToBase64(finalBitmap)
            finalBitmap.recycle()

            Log.d("ScreenshotUtils", "Screenshot capture complete, Base64 length=${base64.length}")
            return base64
        } finally {
            image.close()
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    fun cleanup() {
        Log.d("ScreenshotUtils", "Cleaning up resources")
        virtualDisplay?.release()
        imageReader?.close()
        virtualDisplay = null
        imageReader = null
        isSetup = false
    }
}