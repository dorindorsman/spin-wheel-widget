package com.example.spinwheel

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.example.spinwheel.data.SpinWheelApi
import com.example.spinwheel.data.SpinWheelCacheManager
import com.example.spinwheel.data.SpinWheelRepository
import com.example.spinwheel.prefs.SpinWheelPrefs
import com.example.spinwheel.widget.ui.SpinWheelFramesGenerator
import com.example.spinwheel.widget.ui.SpinWheelWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SpinWheelSdk {

    suspend fun refresh(context: Context, configUrl: String) =
        withContext(Dispatchers.IO) {

            Log.d("SPIN_DEBUG", "refresh started")

            val api = SpinWheelApi()
            val cache = SpinWheelCacheManager(context)
            val prefs = SpinWheelPrefs(context)
            val repo = SpinWheelRepository(api, cache, prefs)

            val cfg = repo.getOrFetchActiveConfig(configUrl)
            Log.d("SPIN_DEBUG", "config loaded")

            repo.ensureAssets(cfg)
            Log.d("SPIN_DEBUG", "assets ensured")

            SpinWheelFramesGenerator.ensureFrames(cache)
            Log.d("SPIN_DEBUG", "frames ensured")

            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(SpinWheelWidget::class.java)
            Log.d("SPIN_DEBUG", "widget ids = ${ids.size}")

            ids.forEach { id ->
                SpinWheelWidget().update(context, id)
            }

            Log.d("SPIN_DEBUG", "refresh finished")
        }
}