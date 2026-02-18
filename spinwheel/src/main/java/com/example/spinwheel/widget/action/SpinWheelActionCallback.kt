package com.example.spinwheel.widget.action

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.spinwheel.prefs.SpinWheelPrefs
import com.example.spinwheel.widget.ui.SpinWheelWidget
import kotlinx.coroutines.delay
import kotlin.random.Random

private val KEY_FRAME_INDEX = intPreferencesKey("wheel_frame_index")
private const val FRAMES_COUNT = 12

class SpinActionCallback : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = SpinWheelPrefs(context)

        // Prevent double-taps while spinning
        if (prefs.isSpinning()) {
            val startedAt = prefs.getSpinStartedAtMs()
            val now = System.currentTimeMillis()

            // If spin flag got stuck, auto-release after a grace period
            val timeoutMs = 8_000L
            val isStuck = startedAt > 0L && (now - startedAt) > timeoutMs

            if (!isStuck) {
                Log.d("SpinActionCallback", "Ignored tap: already spinning")
                return
            }

            Log.w("SpinActionCallback", "Spin flag was stuck. Auto-releasing.")
            prefs.setSpinning(false)
        }

        prefs.setSpinning(true)

        try {
            val safeMin = prefs.getMinSpins().coerceAtLeast(1)
            val safeMax = prefs.getMaxSpins().coerceAtLeast(safeMin)
            val totalDurationMs = prefs.getSpinDurationMs().coerceAtLeast(400L)

            val spins = Random.nextInt(safeMin, safeMax + 1) // inclusive
            val offset = Random.nextInt(1, FRAMES_COUNT)     // 1..11 (never 0)
            val steps = spins * FRAMES_COUNT + offset

            // Base delay so total duration ~ totalDurationMs
            val baseDelay = (totalDurationMs / steps).coerceAtLeast(60L)

            for (i in 0 until steps) {
                updateAppWidgetState(context, glanceId) { state ->
                    val current = state[KEY_FRAME_INDEX] ?: 0
                    val next = (current + 1) % FRAMES_COUNT
                    state[KEY_FRAME_INDEX] = next

                    // Requirement: persist relevant state in SharedPreferences too
                    prefs.setWheelFrameIndex(next)
                }

                SpinWheelWidget().update(context, glanceId)

                // Simple ease-out: delay grows toward end
                val progress = i.toFloat() / steps
                val delayForStep = baseDelay + (baseDelay * progress).toLong()
                delay(delayForStep)
            }

            Log.d(
                "SpinActionCallback",
                "Spin finished frame=${prefs.getWheelFrameIndex()} steps=$steps duration=$totalDurationMs"
            )
        } catch (t: Throwable) {
            Log.e("SpinActionCallback", "Spin failed: ${t.message}", t)
        } finally {
            prefs.setSpinning(false)
        }
    }
}