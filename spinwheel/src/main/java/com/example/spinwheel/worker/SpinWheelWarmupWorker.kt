package com.example.spinwheel.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spinwheel.SpinWheelSdk
import com.example.spinwheel.config.SpinWheelConstants

class SpinWheelWarmupWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            SpinWheelSdk.refreshIfNeeded(
                context = applicationContext,
                configUrl = SpinWheelConstants.CONFIG_URL
            )
            Result.success()
        } catch (_: Throwable) {
            Result.retry()
        }
    }
}