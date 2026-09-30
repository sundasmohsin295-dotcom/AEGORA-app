package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*

data class TrustDevice(
  val name: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector,
  val lastActive: String,
  val risk: String,
  val status: String,
  var isRevoked: Boolean = false
)

@Composable
fun ZeroTrustSecurityScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var mfaEnforced by remember { mutableStateOf(true) }
  var deviceVerification by remember { mutableStateOf(true) }
  var locationCheck by remember { mutableStateOf(true) }
  var behaviorAnalytics by remember { mutableStateOf(true) }

  val devices = remember {
    mutableStateListOf(
      TrustDevice("Windows PC", Icons.Default.Computer, "2 min ago", "Low", "Active"),
      TrustDevice("Android (Pixel 9)", Icons.Default.Smartphone, "18 min ago", "Low", "Active"),
      TrustDevice("MacBook Pro", Icons.Default.Laptop, "1 hr ago", "Medium", "Active"),
      TrustDevice("Unknown Node", Icons.Default.QuestionMark, "2 hr ago", "High", "Blocked", isRevoked = true)
    )
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
  ) {
    val isWideScreen = maxWidth >= 840.dp

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .testTag("zero_trust_security_screen"),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ObsidianSurface)
                .border(1.dp, SlateBorder, CircleShape)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ElectricCyan.copy(alpha = 0.15f))
                .border(1.dp, ElectricCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Zero-Trust Security Center", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
              Text("Never Trust. Always Verify.", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
          }

          Box(
            modifier = Modifier
              .background(TacticalEmeraldDark, RoundedCornerShape(4.dp))
              .border(1.dp, TacticalEmerald, RoundedCornerShape(4.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("Policy Status: Active", color = TacticalEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }

      // 2. 6 Stat Tiles
      item {
        if (isWideScreen) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ZeroTrustTile("Identity", "VERIFIED", ElectricCyan, Icons.Default.Person, Modifier.weight(1f))
            ZeroTrustTile("MFA", "ENABLED", Color(0xFF38BDF8), Icons.Default.VpnKey, Modifier.weight(1f))
            ZeroTrustTile("Active Sessions", "03", TacticalEmerald, Icons.Default.Group, Modifier.weight(1f))
            ZeroTrustTile("Device Trust", "HIGH", TacticalEmerald, Icons.Default.Shield, Modifier.weight(1f))
            ZeroTrustTile("Risk Score", "18 / 100", TacticalAmber, Icons.Default.Speed, Modifier.weight(1f))
            ZeroTrustTile("Passkey", "ACTIVE", CyberViolet, Icons.Default.Key, Modifier.weight(1f))
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              ZeroTrustTile("Identity", "VERIFIED", ElectricCyan, Icons.Default.Person, Modifier.weight(1f))
              ZeroTrustTile("MFA", "ENABLED", Color(0xFF38BDF8), Icons.Default.VpnKey, Modifier.weight(1f))
              ZeroTrustTile("Active Sessions", "03", TacticalEmerald, Icons.Default.Group, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              ZeroTrustTile("Device Trust", "HIGH", TacticalEmerald, Icons.Default.Shield, Modifier.weight(1f))
              ZeroTrustTile("Risk Score", "18 / 100", TacticalAmber, Icons.Default.Speed, Modifier.weight(1f))
              ZeroTrustTile("Passkey", "ACTIVE", CyberViolet, Icons.Default.Key, Modifier.weight(1f))
            }
          }
        }
      }

      // 3. Active Devices & Access Controls (Responsive Split)
      item {
        if (isWideScreen) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            ActiveDevicesList(devices, modifier = Modifier.weight(1.3f))
            AccessControlsPanel(
              mfaEnforced = mfaEnforced,
              onToggleMfa = { mfaEnforced = it },
              deviceVerification = deviceVerification,
              onToggleDevice = { deviceVerification = it },
              locationCheck = locationCheck,
              onToggleLocation = { locationCheck = it },
              behaviorAnalytics = behaviorAnalytics,
              onToggleBehavior = { behaviorAnalytics = it },
              modifier = Modifier.weight(1f)
            )
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ActiveDevicesList(devices, modifier = Modifier.fillMaxWidth())
            AccessControlsPanel(
              mfaEnforced = mfaEnforced,
              onToggleMfa = { mfaEnforced = it },
              deviceVerification = deviceVerification,
              onToggleDevice = { deviceVerification = it },
              locationCheck = locationCheck,
              onToggleLocation = { locationCheck = it },
              behaviorAnalytics = behaviorAnalytics,
              onToggleBehavior = { behaviorAnalytics = it },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ZeroTrustTile(
  label: String,
  status: String,
  accentColor: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(8.dp)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(label, color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
      Spacer(modifier = Modifier.height(2.dp))
      Text(status, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }
  }
}

@Composable
private fun ActiveDevicesList(
  devices: List<TrustDevice>,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text("Active Devices", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

      devices.forEach { dev ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianBackground, RoundedCornerShape(6.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(dev.icon, contentDescription = null, tint = if (dev.isRevoked) HighAlertCrimson else ElectricCyan, modifier = Modifier.size(20.dp))
            Column {
              Text(dev.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text("Last active: ${dev.lastActive}", color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val (riskBg, riskColor) = when (dev.risk) {
              "High" -> HighAlertCrimsonDark to HighAlertCrimson
              "Medium" -> TacticalAmberDark to TacticalAmber
              else -> TacticalEmeraldDark to TacticalEmerald
            }
            Box(
              modifier = Modifier
                .background(riskBg, RoundedCornerShape(3.dp))
                .border(1.dp, riskColor, RoundedCornerShape(3.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(dev.risk, color = riskColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AccessControlsPanel(
  mfaEnforced: Boolean,
  onToggleMfa: (Boolean) -> Unit,
  deviceVerification: Boolean,
  onToggleDevice: (Boolean) -> Unit,
  locationCheck: Boolean,
  onToggleLocation: (Boolean) -> Unit,
  behaviorAnalytics: Boolean,
  onToggleBehavior: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text("Access Controls", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

      PolicyToggleRow("MFA Enforcement", mfaEnforced, onToggleMfa)
      PolicyToggleRow("Device Verification", deviceVerification, onToggleDevice)
      PolicyToggleRow("Location Check", locationCheck, onToggleLocation)
      PolicyToggleRow("Behavior Analytics", behaviorAnalytics, onToggleBehavior)

      Spacer(modifier = Modifier.height(4.dp))

      OutlinedButton(
        onClick = {},
        modifier = Modifier.fillMaxWidth().height(38.dp),
        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text("View Full Policy", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
      }
    }
  }
}

@Composable
private fun PolicyToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = ObsidianBackground,
        checkedTrackColor = ElectricCyan,
        uncheckedThumbColor = TextDim,
        uncheckedTrackColor = ObsidianBackground
      )
    )
  }
}
