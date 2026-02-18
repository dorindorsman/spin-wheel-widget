package com.example.spinwheel.prefs

import android.content.Context
import androidx.core.content.edit

class SpinWheelPrefs(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLastConfigFetchTimeMillis(): Long =
        prefs.getLong(KEY_LAST_CONFIG_FETCH_TIME, 0L)

    fun setLastConfigFetchTimeMillis(value: Long) {
        prefs.edit { putLong(KEY_LAST_CONFIG_FETCH_TIME, value) }
    }

    fun getWheelFrameIndex(): Int =
        prefs.getInt(KEY_WHEEL_FRAME_INDEX, 0)

    fun setWheelFrameIndex(value: Int) {
        prefs.edit { putInt(KEY_WHEEL_FRAME_INDEX, value) }
    }

    fun isSpinning(): Boolean =
        prefs.getBoolean(KEY_IS_SPINNING, false)

    fun setSpinning(value: Boolean) {
        prefs.edit {
            putBoolean(KEY_IS_SPINNING, value)
            if (value) {
                putLong(KEY_SPIN_STARTED_AT_MS, System.currentTimeMillis())
            } else {
                putLong(KEY_SPIN_STARTED_AT_MS, 0L)
            }
        }
    }

    // --- Spin configuration (synced from remote JSON) ---

    fun getSpinDurationMs(): Long =
        prefs.getLong(KEY_SPIN_DURATION_MS, DEFAULT_SPIN_DURATION_MS)

    fun setSpinDurationMs(value: Long) {
        prefs.edit { putLong(KEY_SPIN_DURATION_MS, value) }
    }

    fun getMinSpins(): Int =
        prefs.getInt(KEY_MIN_SPINS, DEFAULT_MIN_SPINS)

    fun setMinSpins(value: Int) {
        prefs.edit { putInt(KEY_MIN_SPINS, value) }
    }

    fun getMaxSpins(): Int =
        prefs.getInt(KEY_MAX_SPINS, DEFAULT_MAX_SPINS)

    fun setMaxSpins(value: Int) {
        prefs.edit { putInt(KEY_MAX_SPINS, value) }
    }

    fun getSpinStartedAtMs(): Long =
        prefs.getLong(KEY_SPIN_STARTED_AT_MS, 0L)

    fun setSpinStartedAtMs(value: Long) {
        prefs.edit { putLong(KEY_SPIN_STARTED_AT_MS, value) }
    }

    private companion object {
        const val PREFS_NAME = "spinwheel_prefs"

        const val KEY_LAST_CONFIG_FETCH_TIME = "last_config_fetch_time"
        const val KEY_WHEEL_FRAME_INDEX = "wheel_frame_index"
        const val KEY_IS_SPINNING = "is_spinning"
        const val KEY_SPIN_STARTED_AT_MS = "spin_started_at_ms"

        const val KEY_SPIN_DURATION_MS = "spin_duration_ms"
        const val KEY_MIN_SPINS = "min_spins"
        const val KEY_MAX_SPINS = "max_spins"

        const val DEFAULT_SPIN_DURATION_MS = 2000L
        const val DEFAULT_MIN_SPINS = 3
        const val DEFAULT_MAX_SPINS = 5
    }
}