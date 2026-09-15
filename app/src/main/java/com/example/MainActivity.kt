package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.network.NetworkMonitorService
import com.example.security.SecurityEnforcer
import com.example.ui.AegoraApp
import com.example.ui.theme.AegoraTheme

class MainActivity : FragmentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    WindowCompat.setDecorFitsSystemWindows(window, false)
    NetworkMonitorService.initialize(applicationContext)

    // RASP (Runtime Application Self-Protection) check:
    val auditReport = SecurityEnforcer.enforce(applicationContext)
    if (auditReport.isCompromised) {
      SecurityEnforcer.clearEncryptedStorage(applicationContext)
    }

    setContent {
      AegoraTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFF000000)
        ) {
          AegoraApp(initialAuditReport = auditReport)
        }
      }
    }
  }
}


