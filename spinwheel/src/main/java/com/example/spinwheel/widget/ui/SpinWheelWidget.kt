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
import com.example.spinwheel.data.SpinWheelCacheManager
import com.example.spinwheel.widget.action.SpinActionCallback

private val KEY_FRAME_INDEX = intPreferencesKey("wheel_frame_index")

class SpinWheelWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            SpinWheelContent(context)
        }
    }

    @Composable
    private fun SpinWheelContent(context: Context) {
        val cache = SpinWheelCacheManager(context)

        val state = currentState<Preferences>()
        val frameIndex = state[KEY_FRAME_INDEX] ?: 0

        val bgBitmap = SpinWheelRenderer.getBitmap(cache.getAssetFile("bg.jpeg"))
        val wheelBitmap = SpinWheelRenderer.getBitmap(cache.getFrameFile(frameIndex))
        val frameBitmap = SpinWheelRenderer.getBitmap(cache.getAssetFile("wheel-frame.png"))
        val spinBitmap = SpinWheelRenderer.getBitmap(cache.getAssetFile("wheel-spin.png"))

        Box(modifier = GlanceModifier.fillMaxSize()) {

            if (bgBitmap != null) {
                Image(
                    provider = ImageProvider(bgBitmap),
                    contentDescription = "Background",
                    modifier = GlanceModifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (wheelBitmap != null) {
                    Image(
                        provider = ImageProvider(wheelBitmap),
                        contentDescription = "Wheel",
                        modifier = GlanceModifier.size(180.dp)
                    )
                }

                if (frameBitmap != null) {
                    Image(
                        provider = ImageProvider(frameBitmap),
                        contentDescription = "Frame",
                        modifier = GlanceModifier.size(200.dp)
                    )
                }

                if (spinBitmap != null) {
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
}