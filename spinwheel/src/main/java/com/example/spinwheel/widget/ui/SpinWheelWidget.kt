package com.example.spinwheel.widget.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.example.spinwheel.data.SpinWheelCacheManager
import com.example.spinwheel.widget.action.SpinActionCallback
import com.example.spinwheel.worker.SpinWheelWarmup
import java.io.File

private val KEY_FRAME_INDEX = intPreferencesKey("wheel_frame_index")

class SpinWheelWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { SpinWheelContent(context) }
    }

    @Composable
    private fun SpinWheelContent(context: Context) {
        val cache = SpinWheelCacheManager(context)

        val hasBg = cache.getAssetFile("bg.jpeg").isReadyImage()
        val hasFrame = cache.getAssetFile("wheel-frame.png").isReadyImage()
        val hasSpin = cache.getAssetFile("wheel-spin.png").isReadyImage()
        val hasWheel = cache.getAssetFile("wheel.png").isReadyImage()
        val hasFrames = cache.framesCount() >= 12

        // Ready only when ALL overlay assets exist AND we have wheel (frames preferred)
        val ready = hasBg && hasFrame && hasSpin && (hasFrames || hasWheel)

        if (!ready) {
            // Kick warmup in background; widget will update after worker finishes (updateAll)
            SpinWheelWarmup.enqueue(context.applicationContext)

            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading…")
            }
            return
        }

        val state = currentState<Preferences>()
        val frameIndex = state[KEY_FRAME_INDEX] ?: 0

        val bgBitmap = SpinWheelRenderer.getBitmap(cache.getAssetFile("bg.jpeg"))
        val frameBitmap = SpinWheelRenderer.getBitmap(cache.getAssetFile("wheel-frame.png"))
        val spinBitmap = SpinWheelRenderer.getBitmap(cache.getAssetFile("wheel-spin.png"))

        val wheelBitmap = if (hasFrames) {
            SpinWheelRenderer.getBitmap(cache.getFrameFile(frameIndex))
        } else {
            SpinWheelRenderer.getBitmap(cache.getAssetFile("wheel.png"))
        }

        // If something was deleted/corrupted between checks, fallback to loading + enqueue
        if (bgBitmap == null || frameBitmap == null || spinBitmap == null || wheelBitmap == null) {
            SpinWheelWarmup.enqueue(context.applicationContext)
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading…")
            }
            return
        }

        Box(modifier = GlanceModifier.fillMaxSize()) {

            Image(
                provider = ImageProvider(bgBitmap),
                contentDescription = "Background",
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(wheelBitmap),
                    contentDescription = "Wheel",
                    modifier = GlanceModifier.size(180.dp)
                )

                Image(
                    provider = ImageProvider(frameBitmap),
                    contentDescription = "Frame",
                    modifier = GlanceModifier.size(200.dp)
                )

                Image(
                    provider = ImageProvider(spinBitmap),
                    contentDescription = "Spin",
                    modifier = GlanceModifier
                        .size(64.dp)
                        .clickable(actionRunCallback<SpinActionCallback>())
                )
            }
        }
    }
}

private fun File.isReadyImage(): Boolean = exists() && length() > 0L