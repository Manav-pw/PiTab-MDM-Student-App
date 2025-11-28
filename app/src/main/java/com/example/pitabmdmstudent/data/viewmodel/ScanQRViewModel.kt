package com.example.pitabmdmstudent.data.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanQRViewModel @Inject constructor() : ViewModel() {

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap = _qrBitmap.asStateFlow()

    fun setCode(code: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _qrBitmap.value = generateQrBitmap(code)
        }
    }
}

fun generateQrBitmap(text: String, size: Int = 600): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap[x, y] =
                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    return bitmap
}
