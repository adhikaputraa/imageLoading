package com.adhika.imageloading.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object DiskCache {
    fun saveBitmap(context: Context, bitmap: Bitmap, filename: String) {
        val file = File(context.cacheDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    fun loadBitmap(context: Context, filename: String): Bitmap? {
        val file = File(context.cacheDir, filename)
        return if (file.exists()) BitmapFactory.decodeFile(file.path) else null
    }
}