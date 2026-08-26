package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.DailyMission
import com.example.model.UserProfile
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.SkillProgressBar
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  onNavigateToJourney: () -> Unit,
  onNavigateToLabs: () -> Unit,
  onNavigateToAi: () -> Unit,
  onNavigateToPassport: () -> Unit,
  onNavigateToLesson: (String) -> Unit,
  onNavigateToIntelligence: () -> Unit,
  onNavigateToCareers: () -> Unit,
  onNavigateToVault: () -> Unit = {},
  onNavigateToCommunity: () -> Unit = {},
  onNavigateToUniversity: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val dailyMission by AegoraRepository.dailyMission.collectAsState()
  val reviewQueue by AegoraRepository.reviewQueue.collectAsState()
  val dueReviewCount = reviewQueue.count { it.isDue }

  val activeCareer = AegoraRepository.careerRoles.find { it.id == userProfile.targetCareerId }
    ?: AegoraRepository.careerRoles.first()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Hero Command Center Banner (Vibrant Purple Container)
    item {
      CyberCard(
        borderColor = CyberBorderSubtle,
        backgroundColor = VibrantPurpleContainer,
        shapeRadius = 28.dp
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "TARGET CAREER TRACK",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = VibrantPurpleOnContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = activeCareer.title,
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
              ),
              color = VibrantPurpleOnContainer
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Phase 1 • Foundational Systems & Telemetry",
              style = MaterialTheme.typography.bodyMedium,
              color = VibrantPurpleOnContainer.copy(alpha = 0.8f)
            )
          }

          // Job Readiness Ring
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.7f))
              .border(2.dp, VibrantPurpleOnContainer, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${userProfile.jobReadinessScore}%",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = VibrantPurpleOnContainer
              )
              Text(
                text = "READY",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = VibrantPurpleOnContainer.copy(alpha = 0.7f)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Next Best Action Prompt
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = Color.White.copy(alpha = 0.85f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "RECOMMENDED SPRINT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                ),
                color = VibrantPurpleOnContainer
              )
              Text(
                text = "Sysmon Event ID 1 & Triage",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
            }

            Button(
              onClick = { onNavigateToLesson("les_102") },
              colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.testTag("home_start_next_action")
            ) {
              Text("Resume", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Knowledge Vault & Spaced Repetition Due Card
    item {
      CyberCard(
        borderColor = CyberBorderSubtle,
        backgroundColor = if (dueReviewCount > 0) VibrantAmberContainer else CyberSurfaceVariant,
        shapeRadius = 24.dp,
        onClick = onNavigateToVault
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (dueReviewCount > 0) Color.White.copy(alpha = 0.8f) else VibrantPurpleContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (dueReviewCount > 0) Icons.Default.PsychologyAlt else Icons.Default.Bookmark,
                contentDescription = null,
                tint = if (dueReviewCount > 0) VibrantAmberOnContainer else VibrantPurpleOnContainer,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = if (dueReviewCount > 0) "SPACED REPETITION QUEUE" else "KNOWLEDGE VAULT",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = if (dueReviewCount > 0) VibrantAmberOnContainer.copy(alpha = 0.8f) else TextSecondaryDark
              )
              Text(
                text = if (dueReviewCount > 0) "$dueReviewCount Flashcards Due for Review" else "Personal Notes & Bookmarks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (dueReviewCount > 0) VibrantAmberOnContainer else TextPrimaryDark
              )
            }
          }

          FilledTonalButton(
            onClick = onNavigateToVault,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = if (dueReviewCount > 0) VibrantAmberOnContainer else VibrantPurpleContainer,
              contentColor = if (dueReviewCount > 0) Color.White else VibrantPurpleOnContainer
            )
          ) {
            Text(if (dueReviewCount > 0) "Review" else "Open Vault", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 2. Today's Daily Cyber Mission (Vibrant Pink Container)
    item {
      CyberCard(
        borderColor = CyberBorderSubtle,
        backgroundColor = VibrantPinkContainer,
        shapeRadius = 28.dp
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.6f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (dailyMission.isCompleted) Icons.Default.CheckCircle else Icons.Default.Shield,
                contentDescription = null,
                tint = VibrantPinkOnContainer,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "DAILY SECURITY SPRINT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = VibrantPinkOnContainer.copy(alpha = 0.7f)
              )
              Text(
                text = dailyMission.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = VibrantPinkOnContainer
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = dailyMission.scenarioContext,
          style = MaterialTheme.typography.bodyMedium,
          color = VibrantPinkOnContainer.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.6f)
          ) {
            Text(
              text = "${dailyMission.difficulty} • ${dailyMission.estimatedTimeMinutes}m • +${dailyMission.xpReward} XP",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = VibrantPinkOnContainer,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }

          if (!dailyMission.isCompleted) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedButton(
                onClick = { AegoraRepository.completeDailyMission() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantPinkOnContainer),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, VibrantPinkOnContainer),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("home_complete_mission_btn")
              ) {
                Text("Verify", fontWeight = FontWeight.Bold)
              }
              Button(
                onClick = { onNavigateToLabs() },
                colors = ButtonDefaults.buttonColors(containerColor = VibrantPinkOnContainer),
                shape = RoundedCornerShape(20.dp)
              ) {
                Text("Lab", color = Color.White, fontWeight = FontWeight.Bold)
              }
            }
          } else {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = VibrantMintContainer,
              border = androidx.compose.foundation.BorderStroke(1.dp, VibrantMintOnContainer.copy(alpha = 0.3f))
            ) {
              Text(
                text = "✓ Solved & Verified",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = VibrantMintOnContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // 3. Two-Column Vibrant Stats Grid (Hydration / Energy style from design)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Active Lab Simulations Card (Vibrant Blue Container)
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantBlueContainer,
          shapeRadius = 28.dp,
          onClick = onNavigateToLabs
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(VibrantBlueOnContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Terminal,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "3/4",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = VibrantBlueOnContainer
          )
          Text(
            text = "Labs Completed",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = VibrantBlueOnContainer.copy(alpha = 0.7f)
          )
        }

        // Verified Credentials Card (Vibrant Mint Container)
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantMintContainer,
          shapeRadius = 28.dp,
          onClick = onNavigateToPassport
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(VibrantMintOnContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "14",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
            color = VibrantMintOnContainer
          )
          Text(
            text = "Verified Badges",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = VibrantMintOnContainer.copy(alpha = 0.7f)
          )
        }
      }
    }

    // Aegora 2.0 Signature Systems Hub (Genome & Reasoning Graph)
    item {
      val genome by AegoraRepository.learningGenome.collectAsState()
      val decayForecasts by AegoraRepository.skillDecayForecasts.collectAsState()
      val highDecayRisk = decayForecasts.firstOrNull { it.daysUntilCriticalDecay <= 7 }

      CyberCard(
        borderColor = CyberCyan.copy(alpha = 0.5f),
        backgroundColor = CyberSurfaceElevated,
        shapeRadius = 24.dp,
        onClick = onNavigateToJourney
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "CYBER LEARNING GENOME",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberCyan
              )
              Text(
                text = "Multi-Vector Capability: ${genome.overallMasteryScore}%",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald)
          ) {
            Text(
              text = "Prereq Health: ${genome.prerequisiteHealthScore}%",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberEmerald,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        if (highDecayRisk != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberAmber.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Decay Forecast: '${highDecayRisk.skillName}' decaying in ${highDecayRisk.daysUntilCriticalDecay} days. Tap to refresh.",
                style = MaterialTheme.typography.labelSmall,
                color = CyberAmber
              )
            }
          }
        }
      }
    }

    // Hands-On Coverage & Sequential Ladders Hub
    item {
      CyberCard(
        borderColor = CyberEmerald.copy(alpha = 0.4f),
        backgroundColor = CyberSurface,
        shapeRadius = 24.dp,
        onClick = onNavigateToLabs
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberEmerald.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "SEQUENTIAL TERMINAL & APPSEC LADDERS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberEmerald
              )
              Text(
                text = "Bandit Linux • PowerShell • Web • Binary Track",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
            }
          }

          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberEmerald)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Zero-hand-holding 16-level terminal ladders and live AppSec patch workflows directly connected to your career story.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark
        )
      }
    }

    // 4. Cyber Skill DNA Breakdown
    item {
      CyberSectionHeader(
        title = "Skill Mastery DNA",
        subtitle = "Multi-dimensional capability scoring",
        actionText = "Full Graph",
        onActionClick = onNavigateToPassport
      )

      CyberCard(
        borderColor = CyberBorder,
        backgroundColor = CyberSurface,
        shapeRadius = 24.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          SkillProgressBar(
            progress = 0.85f,
            label = "Network Telemetry & PCAP Analysis",
            valueText = "85% Mastery",
            barColor = CyberCyan
          )
          SkillProgressBar(
            progress = 0.78f,
            label = "Defensive Security (SIEM & Sysmon)",
            valueText = "78% Mastery",
            barColor = CyberBlue
          )
          SkillProgressBar(
            progress = 0.63f,
            label = "Web Application Security (OWASP)",
            valueText = "63% Mastery",
            barColor = CyberViolet
          )
          SkillProgressBar(
            progress = 0.57f,
            label = "Cloud Security & IAM (AWS/S3)",
            valueText = "57% Mastery",
            barColor = CyberAmber
          )
        }
      }
    }

    // 5. Threat Intelligence Advisory (Alert Card)
    item {
      CyberSectionHeader(
        title = "Threat Intelligence",
        subtitle = "Live CVE & APT Actor Tracker",
        actionText = "Open Intel",
        onActionClick = onNavigateToIntelligence
      )

      val threat = AegoraRepository.threatAdvisories.first()
      CyberCard(
        borderColor = CyberCrimson.copy(alpha = 0.3f),
        backgroundColor = CyberSurface,
        shapeRadius = 24.dp,
        onClick = onNavigateToIntelligence
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberCrimson.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCrimson.copy(alpha = 0.3f))
          ) {
            Text(
              text = "${threat.cveId} • CVSS ${threat.cvssScore}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberCrimson,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Text(
            text = "CRITICAL ADVISORY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = CyberCrimson
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = threat.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = threat.summary,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondaryDark,
          maxLines = 2
        )
      }
    }

    // 6. Quick Access AI & Lab Shortcuts
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // AI Mentor
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberBorderSubtle,
          backgroundColor = CyberSurfaceVariant,
          shapeRadius = 24.dp,
          onClick = onNavigateToAi
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(VibrantPurpleContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = null,
              tint = VibrantPurpleOnContainer,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text("Aegora AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
          Text("SOC Mentor & Socratic Tutor", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        }

        // Career Tracks
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberBorderSubtle,
          backgroundColor = CyberSurfaceVariant,
          shapeRadius = 24.dp,
          onClick = onNavigateToCareers
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(VibrantAmberContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WorkOutline,
              contentDescription = null,
              tint = VibrantAmberOnContainer,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text("Career Hub", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
          Text("6 Cybersecurity Roles", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        }
      }
    }

    // 7. Community & University Portals
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Community Hub
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantBlueContainer.copy(alpha = 0.4f),
          shapeRadius = 24.dp,
          onClick = onNavigateToCommunity
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(VibrantBlueContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Forum,
              contentDescription = null,
              tint = VibrantBlueOnContainer,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text("Community", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
          Text("Peer discussions & cases", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        }

        // University Mode
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantEmeraldContainer.copy(alpha = 0.4f),
          shapeRadius = 24.dp,
          onClick = onNavigateToUniversity
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(VibrantEmeraldContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.School,
              contentDescription = null,
              tint = VibrantEmeraldOnContainer,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text("University Mode", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
          Text("Cohorts & admin portal", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        }
      }
    }
  }
}

