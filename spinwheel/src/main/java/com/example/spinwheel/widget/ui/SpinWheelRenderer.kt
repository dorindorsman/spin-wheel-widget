package com.example.spinwheel.widget.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object SpinWheelRenderer {

    private val memoryCache = ConcurrentHashMap<String, Bitmap>()

    fun getBitmap(file: File): Bitmap? {
        if (!file.exists() || file.length() == 0L) return null

        val key = file.absolutePath
        val cached = memoryCache[key]

        if (cached != null && !cached.isRecycled) {
            return cached
        }

        val bmp = BitmapFactory.decodeFile(file.absolutePath)
        if (bmp != null) {
            memoryCache[key] = bmp
        }
        return bmp
    }

    fun clearMemoryCache() {
        memoryCache.values.forEach { it.recycle() }
        memoryCache.clear()
    }
}