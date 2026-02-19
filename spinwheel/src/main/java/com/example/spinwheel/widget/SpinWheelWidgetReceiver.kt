package com.example.spinwheel.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.spinwheel.widget.ui.SpinWheelWidget
import com.example.spinwheel.worker.SpinWheelWarmup

class SpinWheelWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = SpinWheelWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        SpinWheelWarmup.enqueue(context.applicationContext)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        SpinWheelWarmup.enqueue(context.applicationContext)
    }
}