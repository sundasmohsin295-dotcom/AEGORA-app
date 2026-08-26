package com.example.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * BroadcastReceiver responsible for managing the lifecycle and updates
 * of the 2x2 Aegora Skill Decay Radar Glance Widget.
 */
class SkillDecayRadarWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = SkillDecayRadarWidget()
}
