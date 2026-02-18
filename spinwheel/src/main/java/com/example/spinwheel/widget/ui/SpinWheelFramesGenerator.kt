package com.example.spinwheel.widget.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.example.spinwheel.data.SpinWheelCacheManager
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

object SpinWheelFramesGenerator {

    private const val FRAMES_COUNT = 12

    fun ensureFrames(cache: SpinWheelCacheManager) {
        if (cache.framesCount() >= FRAMES_COUNT) return

        cache.clearFrames()

        // Clear in-memory bitmaps because frame files will be recreated
        SpinWheelRenderer.clearMemoryCache()

        val wheelBitmap = SpinWheelRenderer.getBitmap(cache.getAssetFile("wheel.png")) ?: return

        val size = wheelBitmap.width.coerceAtLeast(wheelBitmap.height)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        for (i in 0 until FRAMES_COUNT) {
            val angle = (360f / FRAMES_COUNT) * i

            val out = createBitmap(size, size)
            val canvas = Canvas(out)

            val matrix = Matrix().apply {
                // move to center
                postTranslate(
                    (size - wheelBitmap.width) / 2f,
                    (size - wheelBitmap.height) / 2f
                )
                // rotate around center of output
                postRotate(angle, size / 2f, size / 2f)
            }

            canvas.drawBitmap(wheelBitmap, matrix, paint)

            val frameFile = cache.getFrameFile(i)
            FileOutputStream(frameFile).use { fos ->
                out.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            out.recycle()
        }
    }
}