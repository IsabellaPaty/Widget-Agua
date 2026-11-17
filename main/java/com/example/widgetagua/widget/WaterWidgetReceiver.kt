package com.example.widgetagua.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Receiver do App Widget declarado no AndroidManifest.xml.
 *
 * Toda a renderização é delegada ao [WaterWidget] (Jetpack Glance).
 */
class WaterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WaterWidget()
}
