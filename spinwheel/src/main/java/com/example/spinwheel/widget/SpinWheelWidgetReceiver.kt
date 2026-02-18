package com.example.spinwheel.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.spinwheel.widget.ui.SpinWheelWidget

class SpinWheelWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SpinWheelWidget()
}