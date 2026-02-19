package com.example.spinwheel.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object SpinWheelWarmup {
    private const val UNIQUE = "SpinWheelWarmup"

    fun enqueue(context: Context) {
        val req = OneTimeWorkRequestBuilder<SpinWheelWarmupWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE,
            ExistingWorkPolicy.KEEP,
            req
        )
    }
}