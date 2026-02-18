package com.example.spinwheel

import android.content.Context
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

    suspend fun refresh(context: Context, configUrl: String) = withContext(Dispatchers.IO) {
        val api = SpinWheelApi()
        val cache = SpinWheelCacheManager(context)
        val prefs = SpinWheelPrefs(context)
        val repo = SpinWheelRepository(api, cache, prefs)

        val cfg = repo.getOrFetchActiveConfig(configUrl)
        repo.ensureAssets(cfg)

        // Ensure frames exist (needed for "spin" visuals)
        SpinWheelFramesGenerator.ensureFrames(cache)

        // Update all widget instances
        val manager = GlanceAppWidgetManager(context)
        val ids = manager.getGlanceIds(SpinWheelWidget::class.java)
        ids.forEach { id -> SpinWheelWidget().update(context, id) }
    }
}