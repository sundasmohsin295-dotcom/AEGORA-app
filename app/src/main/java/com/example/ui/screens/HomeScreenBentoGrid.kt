package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberTwinRadar3D
import com.example.ui.components.PalantirMatteButton
import com.example.ui.components.RadarAxisData
import com.example.ui.overscroll.drawCyberOverscroll
import com.example.ui.overscroll.rememberCyberOverscrollEffect

// STRICT ENTERPRISE OBSIDIAN COLOR TOKENS
private val DeepSpaceCanvas = Color(0xFF050B14)
private val MatteSteelCard = Color(0xFF0B1528)
private val GlowingCobaltPrimary = Color(0xFF3B82F6)
private val EmeraldVerified = Color(0xFF34D399)
private val HairlineBorder = Color(0xFF1E3A5F)
private val CyanAccent = Color(0xFF22D3EE)
private val SubtextSlate = Color(0xFF94A3B8)
private val HeadingWhite = Color(0xFFF8FAFC)

data class OsintAction(
  val id: String,
  val title: String,
  val icon: ImageVector,
  val tag: String,
  val color: Color
)

/**
 * HOME SCREEN BENTO GRID
 *
 * Implements the Enterprise Obsidian Bento Box Architecture:
 * - Deep Space Canvas: #050B14
 * - Matte Steel Cards: #0B1528
 * - Glowing Cobalt (Primary): #3B82F6
 * - Emerald (Verified): #34D399
 * - 1.dp hairline border (#1E3A5F) with 16.dp rounded corners
 * - Seamless LazyVerticalStaggeredGrid wrapping 3D Radar, Next Move Hero, and OSINT Pills
 */
@Composable
fun HomeScreenBentoGrid(
  onStartDuel: () -> Unit = {},
  onSelectOsint: (String) -> Unit = {},
  onOpenRadarDetail: () -> Unit = {},
  onOpenPaywall: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current

  val osintActions = remember {
    listOf(
      OsintAction("pcap", "PCAP Dissect", Icons.Default.FilterAlt, "DPI", GlowingCobaltPrimary),
      OsintAction("dns", "DNS Tunnel Probe", Icons.Default.Language, "Covert", CyanAccent),
      OsintAction("c2", "Cobalt Strike Hunter", Icons.Default.Radar, "C2", EmeraldVerified),
      OsintAction("mem", "Memory Dump Ingest", Icons.Default.Memory, "Forensics", Color(0xFFA855F7)),
      OsintAction("auth", "Credential Exfiltration", Icons.Default.Key, "Auth", Color(0xFFF59E0B))
    )
  }

  val radarScores = remember {
    listOf(
      RadarAxisData("Investigation", 0.88f),
      RadarAxisData("Reasoning", 0.74f),
      RadarAxisData("Tech Skill", 0.92f),
      RadarAxisData("Evidence", 0.68f),
      RadarAxisData("AI Oversight", 0.82f)
    )
  }

  var isHeroEngaging by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  val cyberOverscroll = rememberCyberOverscrollEffect()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(DeepSpaceCanvas)
      .testTag("home_screen_bento_grid"),
    containerColor = DeepSpaceCanvas,
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
          .padding(16.dp)
          .testTag("bento_grid_snackbar_host")
      ) { data ->
        Snackbar(
          modifier = Modifier.border(BorderStroke(1.dp, HairlineBorder), RoundedCornerShape(8.dp)),
          containerColor = MatteSteelCard,
          contentColor = GlowingCobaltPrimary,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = data.visuals.message,
            color = GlowingCobaltPrimary,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
          )
        }
      }
    }
  ) { innerPadding ->
    LazyVerticalStaggeredGrid(
      columns = StaggeredGridCells.Adaptive(minSize = 340.dp),
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp)
        .drawCyberOverscroll(cyberOverscroll),
      verticalItemSpacing = 16.dp,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {

      // ------------------------------------------------------------------------
      // 1. TOP STATUS & BRANDING BANNER (Full Line Span)
      // ------------------------------------------------------------------------
      item(span = StaggeredGridItemSpan.FullLine) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(EmeraldVerified)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "OPERATOR CLEARANCE: TIER-1 SOC",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = EmeraldVerified,
                letterSpacing = 1.sp
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "AEGORA COMMAND",
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = HeadingWhite
            )
          }

          // Hardware Attestation Tag
          Surface(
            color = MatteSteelCard,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, HairlineBorder),
            modifier = Modifier.clickable {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onOpenPaywall()
            }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "FIPS 140-3",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = HeadingWhite
              )
            }
          }
        }
      }

      // ------------------------------------------------------------------------
      // 2. "NEXT MOVE" HERO CARD (Glass-Pane Matte Steel)
      // ------------------------------------------------------------------------
      item(span = StaggeredGridItemSpan.FullLine) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_next_move_hero"),
          color = MatteSteelCard,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, HairlineBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = GlowingCobaltPrimary.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(6.dp),
                  border = BorderStroke(1.dp, GlowingCobaltPrimary.copy(alpha = 0.4f))
                ) {
                  Text(
                    text = "ACTIVE THREAT DUEL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = GlowingCobaltPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "STAGE 04",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = SubtextSlate
                )
              }

              Text(
                text = "+450 XP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = EmeraldVerified
              )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Adversary Duel: Operation Aurora Infiltration",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HeadingWhite
              )
              Text(
                text = "Autonomous AI Chaos Monkey planted a covert ICMP tunnel hallucination into live telemetry. Intercept and verify.",
                fontSize = 13.sp,
                color = SubtextSlate,
                lineHeight = 18.sp
              )
            }

            // High-Precision Telemetry Metrics Strip (Monospace with Glowing Emerald/Cobalt)
            Surface(
              color = Color(0x33000000),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, HairlineBorder.copy(alpha = 0.6f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                  Text(
                    text = "SRC: 10.0.4.18:49822",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = GlowingCobaltPrimary,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "DST: 20.190.159.23:443",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold
                  )
                }
                Text(
                  text = "SCORE: 96/100",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = EmeraldVerified,
                  fontWeight = FontWeight.Black
                )
              }
            }

            // Action Button with State Locking & Anti-Spam
            PalantirMatteButton(
              text = "ENGAGE ADVERSARY DUEL",
              onClick = {
                if (!isHeroEngaging) {
                  isHeroEngaging = true
                  onStartDuel()
                }
              },
              enabled = !isHeroEngaging,
              isLoading = isHeroEngaging,
              containerColor = GlowingCobaltPrimary,
              contentColor = Color.White,
              borderColor = GlowingCobaltPrimary,
              shape = RoundedCornerShape(12.dp),
              isMonospace = true,
              testTag = "btn_start_hero_duel",
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
            )
          }
        }
      }

      // ------------------------------------------------------------------------
      // 3. 3D CYBER TWIN RADAR CARD (Full Line or Adaptive Bento Tile)
      // ------------------------------------------------------------------------
      item(span = StaggeredGridItemSpan.FullLine) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("bento_radar_tile")
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onOpenRadarDetail()
            },
          color = MatteSteelCard,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, HairlineBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Radar,
                  contentDescription = null,
                  tint = GlowingCobaltPrimary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "CYBER TWIN // 3D CAPABILITY RADAR",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  color = HeadingWhite
                )
              }

              Text(
                text = "TAP TO EXPAND",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = CyanAccent
              )
            }

            // Embedded Custom Canvas 3D Isometric Radar
            CyberTwinRadar3D(
              axes = radarScores,
              modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
            )

            // Five Axis Metric Badges
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              radarScores.forEach { axis ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "${(axis.score * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = when {
                      axis.score >= 0.85f -> EmeraldVerified
                      axis.score >= 0.70f -> GlowingCobaltPrimary
                      else -> CyanAccent
                    }
                  )
                  Text(
                    text = axis.name.take(4).uppercase(),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SubtextSlate
                  )
                }
              }
            }
          }
        }
      }

      // ------------------------------------------------------------------------
      // 4. OSINT QUICK-ACTION PILLS (Bento Section)
      // ------------------------------------------------------------------------
      item(span = StaggeredGridItemSpan.FullLine) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "OSINT QUICK-ACTION PIPELINES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = SubtextSlate,
            letterSpacing = 1.sp
          )

          // Grid of Action Pills
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            osintActions.forEach { action ->
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("bento_osint_${action.id}")
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectOsint(action.id)
                  },
                color = MatteSteelCard,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, HairlineBorder)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(action.color.copy(alpha = 0.15f))
                        .border(1.dp, action.color.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = action.color,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = action.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = HeadingWhite
                      )
                      Text(
                        text = "Real-time Scapy ingestion pipeline",
                        fontSize = 10.sp,
                        color = SubtextSlate
                      )
                    }
                  }

                  Surface(
                    color = action.color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = action.tag,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      color = action.color,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // ------------------------------------------------------------------------
      // 5. SECURE HARDWARE ENCLAVE FOOTER (Obsidian Glass Tile)
      // ------------------------------------------------------------------------
      item(span = StaggeredGridItemSpan.FullLine) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MatteSteelCard,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, HairlineBorder)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = EmeraldVerified,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "HARDWARE SECURE ENCLAVE ATTESTATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = EmeraldVerified
              )
              Text(
                text = "Continuous biometric re-verification and Merkle root integrity check passed.",
                fontSize = 11.sp,
                color = SubtextSlate
              )
            }
          }
        }
      }
    }
  }
}
