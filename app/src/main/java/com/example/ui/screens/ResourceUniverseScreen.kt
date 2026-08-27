package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.data.ResourceUniverseRepository
import com.example.model.*
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.HexagonShape
import com.example.ui.theme.*

/**
 * AEGORA Resource Intelligence Universe & Research Hub (V13 Engine).
 * Discovers, organizes, scores, and connects the world's cybersecurity knowledge
 * (Books, Standards, RFCs, Papers, Advisories, Cheat Sheets, Case Studies)
 * directly into the learner's dynamic career path, skill graph, and hands-on cyber ranges.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceUniverseScreen(
  onNavigateBack: () -> Unit,
  onNavigateToLab: (String) -> Unit,
  onAskAiAboutResource: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf(ResourceCategory.ALL) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedTrustFilter by remember { mutableStateOf<ResourceTrustLevel?>(null) }
  var activeDetailResource by remember { mutableStateOf<ResourceKnowledgeItem?>(null) }

  val allResources by ResourceUniverseRepository.resources.collectAsState()
  val userProfile by AegoraRepository.userProfile.collectAsState()

  val filteredResources = remember(allResources, selectedCategory, searchQuery, selectedTrustFilter) {
    allResources.filter { res ->
      (selectedCategory == ResourceCategory.ALL || res.category == selectedCategory) &&
          (selectedTrustFilter == null || res.trustLevel == selectedTrustFilter) &&
          (searchQuery.isBlank() ||
              res.title.contains(searchQuery, ignoreCase = true) ||
              res.subtitle.contains(searchQuery, ignoreCase = true) ||
              res.authorOrOrg.contains(searchQuery, ignoreCase = true) ||
              res.targetedSkills.any { it.contains(searchQuery, ignoreCase = true) } ||
              res.whyThisMatters.contains(searchQuery, ignoreCase = true))
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "RESOURCE UNIVERSE",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                ),
                color = NeonCyan
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = NeonCyan.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "V13 INTEL",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  color = NeonCyan,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Authoritative Books, Standards, RFCs & Research Graph",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
      )
    },
    containerColor = CyberBackground,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(bottom = 32.dp)
    ) {
      // 1. Curated Path Banner (Connected to User Target Career)
      item {
        Surface(
          shape = ChamferedCutCornerShape,
          color = CyberSurfaceElevated,
          border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(HexagonShape)
                    .background(NeonCyan.copy(alpha = 0.18f))
                    .border(1.dp, NeonCyan, HexagonShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.MenuBook, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "TARGET CAREER GRAPH",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 0.8.sp
                    ),
                    color = NeonCyan
                  )
                  Text(
                    text = "Curated for ${userProfile.callsign} (${userProfile.targetCareerId.replace("_", " ").uppercase()})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberEmerald.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "VERIFIED ONLY",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  color = CyberEmerald,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Every knowledge item in this repository is graded with transparent authority metrics and connected directly to hands-on cyber range labs & skill verification milestones.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }
      }

      // 2. Search Box
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by topic, RFC, book, author, tool...", color = TextSecondaryDark) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryDark)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberBorder,
            focusedContainerColor = CyberSurfaceVariant,
            unfocusedContainerColor = CyberSurfaceVariant,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          modifier = Modifier.fillMaxWidth().testTag("resource_search_input")
        )
      }

      // 3. Category Horizontal Filter Pills
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(ResourceCategory.entries) { category ->
            val isSelected = selectedCategory == category
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = category },
              label = { Text(category.label) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                selectedLabelColor = NeonCyan,
                containerColor = CyberSurfaceVariant,
                labelColor = TextSecondaryDark
              ),
              border = BorderStroke(1.dp, if (isSelected) NeonCyan else CyberBorderSubtle),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      // 4. Trust Level Filter Chips (Official vs Academic vs Industry)
      item {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "TRUST LEVEL:",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = TextSecondaryDark
          )

          ResourceTrustLevel.entries.forEach { trust ->
            val isSelected = selectedTrustFilter == trust
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) Color(trust.badgeColor).copy(alpha = 0.2f) else CyberSurfaceVariant,
              border = BorderStroke(1.dp, if (isSelected) Color(trust.badgeColor) else CyberBorderSubtle),
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  selectedTrustFilter = if (isSelected) null else trust
                }
            ) {
              Text(
                text = trust.name,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = if (isSelected) Color(trust.badgeColor) else TextSecondaryDark,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // 5. Section Header & Count
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${filteredResources.size} KNOWLEDGE ITEMS FOUND",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = TextSecondaryDark
          )
        }
      }

      // 6. Resources List
      if (filteredResources.isEmpty()) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = CyberSurface,
            border = BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(48.dp))
              Spacer(modifier = Modifier.height(12.dp))
              Text("No resources matching your criteria", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
              Text("Try searching for RFC, NIST, ATT&CK, or clear active filters.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          }
        }
      } else {
        items(filteredResources, key = { it.id }) { resource ->
          ResourceKnowledgeCard(
            resource = resource,
            onCardClick = { activeDetailResource = resource },
            onToggleBookmark = { ResourceUniverseRepository.toggleBookmark(resource.id) },
            onNavigateToLab = {
              resource.relatedLabId?.let { onNavigateToLab(it) }
            },
            onAskAi = {
              onAskAiAboutResource(resource.title, resource.whyThisMatters)
            }
          )
        }
      }
    }
  }

  // Deep-Dive Resource Modal Dialog
  activeDetailResource?.let { res ->
    ResourceDetailModal(
      resource = res,
      onDismiss = { activeDetailResource = null },
      onNavigateToLab = {
        activeDetailResource = null
        res.relatedLabId?.let { onNavigateToLab(it) }
      },
      onAskAi = {
        activeDetailResource = null
        onAskAiAboutResource(res.title, res.whyThisMatters)
      }
    )
  }
}

@Composable
fun ResourceKnowledgeCard(
  resource: ResourceKnowledgeItem,
  onCardClick: () -> Unit,
  onToggleBookmark: () -> Unit,
  onNavigateToLab: () -> Unit,
  onAskAi: () -> Unit,
  modifier: Modifier = Modifier
) {
  val trustColor = Color(resource.trustLevel.badgeColor)

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, if (resource.isBookmarked) NeonCyan else CyberBorderSubtle),
    modifier = modifier
      .fillMaxWidth()
      .clip(ChamferedCutCornerShape)
      .clickable { onCardClick() }
      .testTag("resource_card_${resource.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Trust Badge + Time + Bookmark
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = trustColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, trustColor.copy(alpha = 0.6f))
        ) {
          Text(
            text = resource.trustLevel.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = trustColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = resource.readingOrStudyTime,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = onToggleBookmark,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = if (resource.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (resource.isBookmarked) NeonCyan else TextSecondaryDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title & Subtitle
      Text(
        text = resource.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )
      Text(
        text = "${resource.subtitle} • ${resource.authorOrOrg}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Transparent Quality Score Bar
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberSurfaceElevated,
        border = BorderStroke(1.dp, CyberBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "QUALITY INDEX: ",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              ),
              color = TextSecondaryDark
            )
            Text(
              text = "${resource.quality.overallScore}/100",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              ),
              color = CyberEmerald
            )
          }

          Text(
            text = "Auth ${resource.quality.authorityScore} • Pract ${resource.quality.practicalValueScore} • Rel ${resource.quality.careerRelevanceScore}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp
            ),
            color = TextSecondaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Why This Matters Explanatory Callout
      Text(
        text = "WHY THIS MATTERS:",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp
        ),
        color = NeonCyan
      )
      Text(
        text = resource.whyThisMatters,
        style = MaterialTheme.typography.bodySmall,
        color = TextPrimaryDark,
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Related Lab Banner & AI Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (resource.relatedLabTitle != null) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberEmerald.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .clickable { onNavigateToLab() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Apply in Cyber Range →",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberEmerald
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CyberViolet.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, CyberViolet.copy(alpha = 0.4f)),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onAskAi() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Ask AI",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              color = CyberViolet
            )
          }
        }
      }
    }
  }
}

@Composable
fun ResourceDetailModal(
  resource: ResourceKnowledgeItem,
  onDismiss: () -> Unit,
  onNavigateToLab: () -> Unit,
  onAskAi: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color(resource.trustLevel.badgeColor).copy(alpha = 0.2f),
          border = BorderStroke(1.dp, Color(resource.trustLevel.badgeColor))
        ) {
          Text(
            text = "${resource.category.label} • ${resource.trustLevel.label}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = Color(resource.trustLevel.badgeColor),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = resource.title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = TextPrimaryDark
        )
        Text(
          text = "${resource.subtitle} (${resource.publicationYear})",
          style = MaterialTheme.typography.labelMedium,
          color = TextSecondaryDark
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Detailed transparent score
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CyberSurfaceElevated,
          border = BorderStroke(1.dp, CyberBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text("TRANSPARENT QUALITY INDEX", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = NeonCyan)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Authority: ${resource.quality.authorityScore}/100", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
              Text("Practical: ${resource.quality.practicalValueScore}/100", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Career Relevance: ${resource.quality.careerRelevanceScore}/100", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
              Text("Recency: ${resource.quality.recencyScore}/100", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            }
          }
        }

        // Key Takeaways
        Column {
          Text("CORE METHODOLOGICAL TAKEAWAYS:", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = CyberEmerald)
          Spacer(modifier = Modifier.height(4.dp))
          resource.keyTakeaways.forEach { takeaway ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
              Text("• ", color = CyberEmerald, fontWeight = FontWeight.Bold)
              Text(takeaway, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
            }
          }
        }

        // Why This Matters
        Column {
          Text("CAREER IMPACT:", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = NeonCyan)
          Spacer(modifier = Modifier.height(2.dp))
          Text(resource.whyThisMatters, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
        }

        if (resource.relatedLabTitle != null) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberEmerald.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberEmerald),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToLab() }
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text("CONNECTED LAB CHALLENGE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = CyberEmerald)
                Text(resource.relatedLabTitle, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onAskAi,
        colors = ButtonDefaults.buttonColors(containerColor = CyberViolet)
      ) {
        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Ask Socratic AI")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close", color = TextSecondaryDark)
      }
    }
  )
}
