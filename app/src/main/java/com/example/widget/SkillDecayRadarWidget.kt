package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.R
import com.example.data.AegoraRepository

/**
 * 2x2 Jetpack Glance App Widget for AEGORA Cyber Defense Academy.
 * Displays real-time 'Skill Decay Radar' score and active CTF streaks
 * styled in an obsidian cyber-dark aesthetic.
 */
class SkillDecayRadarWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      SkillDecayRadarContent()
    }
  }

  @Composable
  private fun SkillDecayRadarContent() {
    // Collect repository state snapshots
    val userProfile = AegoraRepository.userProfile.value
    val decayForecasts = AegoraRepository.skillDecayForecasts.value

    val decayScore = if (decayForecasts.isNotEmpty()) {
      decayForecasts.map { it.retentionScore }.average().toInt()
    } else {
      68
    }

    val criticalDecayCount = decayForecasts.count { it.riskLevel == "CRITICAL" || it.riskLevel == "HIGH" }
    val highestRiskSkill = decayForecasts
      .filter { it.riskLevel == "CRITICAL" || it.riskLevel == "HIGH" }
      .minByOrNull { it.retentionScore }

    val streakDays = userProfile.currentStreak
    val ctfsCount = userProfile.completedCtfsCount

    // Cyber Dark Palette ColorProviders
    val bgObsidian = ColorProvider(Color(0xFF07070A))
    val cardSurface = ColorProvider(Color(0xFF11111A))
    val innerCardSurface = ColorProvider(Color(0xFF171724))
    val neonCyan = ColorProvider(Color(0xFF00F0FF))
    val neonEmerald = ColorProvider(Color(0xFF00FF41))
    val terminalAmber = ColorProvider(Color(0xFFFFB000))
    val alertCrimson = ColorProvider(Color(0xFFFF003C))
    val textPrimary = ColorProvider(Color(0xFFF0F4F8))
    val textMuted = ColorProvider(Color(0xFF8A8A9E))

    val scoreColor = when {
      decayScore >= 80 -> neonEmerald
      decayScore >= 60 -> neonCyan
      decayScore >= 45 -> terminalAmber
      else -> alertCrimson
    }

    Box(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(bgObsidian)
        .cornerRadius(18.dp)
        .padding(10.dp)
        .clickable(actionStartActivity<MainActivity>())
    ) {
      Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
      ) {
        // --- 1. Header Bar ---
        Row(
          modifier = GlanceModifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Image(
            provider = ImageProvider(R.drawable.ic_widget_radar),
            contentDescription = "Radar Icon",
            modifier = GlanceModifier.size(16.dp)
          )

          Spacer(modifier = GlanceModifier.width(5.dp))

          Text(
            text = "AEGORA",
            style = TextStyle(
              color = neonCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          )

          Text(
            text = " // DECAY RADAR",
            style = TextStyle(
              color = textMuted,
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium
            )
          )

          Spacer(modifier = GlanceModifier.defaultWeight())

          // Pulse Dot
          Box(
            modifier = GlanceModifier
              .size(6.dp)
              .background(neonEmerald)
              .cornerRadius(3.dp)
          ) {}
        }

        // --- 2. Main Skill Decay Metric Card ---
        Box(
          modifier = GlanceModifier
            .fillMaxWidth()
            .background(cardSurface)
            .cornerRadius(12.dp)
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Retention Gauge Number
            Column(
              horizontalAlignment = Alignment.Start
            ) {
              Text(
                text = "$decayScore%",
                style = TextStyle(
                  color = scoreColor,
                  fontSize = 26.sp,
                  fontWeight = FontWeight.Bold
                )
              )
              Text(
                text = "SKILL RETENTION",
                style = TextStyle(
                  color = textMuted,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            // Status Indicator & At-Risk Summary
            Column(
              horizontalAlignment = Alignment.End
            ) {
              Box(
                modifier = GlanceModifier
                  .background(if (criticalDecayCount > 0) ColorProvider(Color(0xFF2E000A)) else ColorProvider(Color(0xFF002914)))
                  .cornerRadius(6.dp)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (criticalDecayCount > 0) "$criticalDecayCount AT RISK" else "OPTIMAL",
                  style = TextStyle(
                    color = if (criticalDecayCount > 0) alertCrimson else neonEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                )
              }

              Spacer(modifier = GlanceModifier.height(3.dp))

              Text(
                text = highestRiskSkill?.let { "${it.skillName.take(13)}.." } ?: "All Skills Fresh",
                style = TextStyle(
                  color = textPrimary,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Normal
                )
              )
            }
          }
        }

        Spacer(modifier = GlanceModifier.height(6.dp))

        // --- 3. Active CTF Streaks & Stats Row ---
        Row(
          modifier = GlanceModifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Streak Capsule
          Box(
            modifier = GlanceModifier
              .defaultWeight()
              .background(innerCardSurface)
              .cornerRadius(10.dp)
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Row(
              modifier = GlanceModifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Image(
                provider = ImageProvider(R.drawable.ic_widget_flame),
                contentDescription = "Streak Flame",
                modifier = GlanceModifier.size(15.dp)
              )
              Spacer(modifier = GlanceModifier.width(4.dp))
              Column {
                Text(
                  text = "${streakDays}D STREAK",
                  style = TextStyle(
                    color = terminalAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "ACTIVE",
                  style = TextStyle(
                    color = textMuted,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }
          }

          Spacer(modifier = GlanceModifier.width(6.dp))

          // CTFs Conquered Capsule
          Box(
            modifier = GlanceModifier
              .defaultWeight()
              .background(innerCardSurface)
              .cornerRadius(10.dp)
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Row(
              modifier = GlanceModifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Image(
                provider = ImageProvider(R.drawable.ic_widget_shield),
                contentDescription = "CTF Shield",
                modifier = GlanceModifier.size(15.dp)
              )
              Spacer(modifier = GlanceModifier.width(4.dp))
              Column {
                Text(
                  text = "$ctfsCount CTFS",
                  style = TextStyle(
                    color = neonEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "CONQUERED",
                  style = TextStyle(
                    color = textMuted,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}
