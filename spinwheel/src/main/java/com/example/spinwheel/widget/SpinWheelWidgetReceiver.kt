package com.example.spinwheel.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.spinwheel.SpinWheelSdk
import com.example.spinwheel.config.SpinWheelConstants
import com.example.spinwheel.widget.ui.SpinWheelWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpinWheelWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = SpinWheelWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                SpinWheelSdk.refresh(
                    context = context.applicationContext,
                    configUrl = SpinWheelConstants.CONFIG_URL
                )
            } catch (t: Throwable) {
                Log.e("SPIN_DEBUG", "Widget initial refresh failed", t)
            }
        }
    }
}
