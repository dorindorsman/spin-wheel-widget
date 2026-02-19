package com.example.spinwheel

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.spinwheel.data.SpinWheelApi
import com.example.spinwheel.data.SpinWheelCacheManager
import com.example.spinwheel.data.SpinWheelRepository
import com.example.spinwheel.prefs.SpinWheelPrefs
import com.example.spinwheel.widget.ui.SpinWheelFramesGenerator
import com.example.spinwheel.widget.ui.SpinWheelWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

object SpinWheelSdk {

    private val refreshMutex = Mutex()

    suspend fun refreshIfNeeded(context: Context, configUrl: String) = withContext(Dispatchers.IO) {
        refreshMutex.withLock {
            val cache = SpinWheelCacheManager(context)
            val prefs = SpinWheelPrefs(context)

            val readyFiles =
                cache.getAssetFile("bg.jpeg").exists() &&
                        cache.getAssetFile("wheel.png").exists() &&
                        cache.getAssetFile("wheel-frame.png").exists() &&
                        cache.getAssetFile("wheel-spin.png").exists() &&
                        cache.framesCount() >= 12

            val cacheValid = runCatching {
                val api = SpinWheelApi()
                val repo = SpinWheelRepository(api, cache, prefs)
                repo.isCacheStillValidFromDisk()
            }.getOrDefault(false)

            if (readyFiles && cacheValid) {
                Log.d("SPIN_DEBUG", "refreshIfNeeded: cache ready & valid -> updateAll only")
                SpinWheelWidget().updateAll(context)
                return@withLock
            }

            Log.d("SPIN_DEBUG", "refreshIfNeeded: doing refresh")
            refresh(context, configUrl)
        }
    }

    suspend fun refresh(context: Context, configUrl: String) = withContext(Dispatchers.IO) {
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

        SpinWheelWidget().updateAll(context)

        Log.d("SPIN_DEBUG", "refresh finished")
    }
}