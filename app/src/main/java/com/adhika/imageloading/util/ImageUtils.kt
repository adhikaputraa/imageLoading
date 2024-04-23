package com.adhika.imageloading.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL
import kotlin.math.roundToInt

object ImageUtils {

    suspend fun downloadAndCacheImage(context: Context, imageUrl: String): Bitmap? {
        val key = imageUrl.hashCode().toString()

        // Check memory cache first
        MemoryCache.get(key)?.let { return it }

        // Check disk cache
        val diskBitmap = DiskCache.loadBitmap(context, key)
        diskBitmap?.let {
            MemoryCache.put(key, it)
            return it
        }

        // Download and cache
        return try {
            val bitmap = resizeBitmap(downloadImage(imageUrl), getNewWidth(context))
            bitmap?.let {
                MemoryCache.put(key, it)
                DiskCache.saveBitmap(context, it, key)
            }
            bitmap
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun downloadImage(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(imageUrl).openConnection()
            connection.connect()
            val input = connection.getInputStream()

            val bitmap = BitmapFactory.decodeStream(input)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream)
            val byteArray = stream.toByteArray()
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun resizeBitmap(bitmap: Bitmap?, newWidth: Int): Bitmap? {
        bitmap?.let {
            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

            // Calculate the new height to maintain the aspect ratio
            val newHeight = (newWidth / aspectRatio).roundToInt()

            // Create a new bitmap with the new width and height
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
        }
        return null
    }

    private fun getNewWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels / 4
    }
}