package com.example.qr_db

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

fun generateQrCode(text: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        // Устанавливаем подсказку MARGIN в 0, чтобы убрать белые поля вокруг кода
        val hints = mapOf(EncodeHintType.MARGIN to 0)
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
