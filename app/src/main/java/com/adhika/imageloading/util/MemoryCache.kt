package com.adhika.imageloading.util

import android.graphics.Bitmap
import android.util.LruCache

object MemoryCache {

    private val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private val bitmapCache = LruCache<String, Bitmap>(cacheSize)

    fun get(key: String): Bitmap? = bitmapCache.get(key)

    fun put(key: String, bitmap: Bitmap) {
        bitmapCache.put(key, bitmap)
    }
}