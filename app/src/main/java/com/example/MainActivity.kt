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
import android.view.WindowManager
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.network.NetworkMonitorService
import com.example.security.IntentFirewall
import com.example.security.SecurityEnforcer
import com.example.security.ZeroDayAppSecurityContainer
import com.example.ui.AegoraApp
import com.example.ui.theme.AegoraTheme

class MainActivity : FragmentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // PHASE 18 ZERO-DAY IMMUNITY: Screen Snapshot Shield (Anti-Screenshot / Anti-Screen Recording)
    window.setFlags(
      WindowManager.LayoutParams.FLAG_SECURE,
      WindowManager.LayoutParams.FLAG_SECURE
    )

    enableEdgeToEdge()
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // Armed Zero-Crash Exception Handler (Captures unhandled exceptions before system dialogue)
    com.example.util.GlobalExceptionHandler.initialize(applicationContext)

    // PHASE 21 STRICT IPC ISOLATION & INTENT FIREWALL
    IntentFirewall.validateAndSanitize(this, intent)

    NetworkMonitorService.initialize(applicationContext)

    // PHASE 25 ACCESSIBILITY ABUSE SHIELD: Detects untrusted screen-scraping services
    com.example.security.AccessibilityShield.registerListener(this)

    // RASP (Runtime Application Self-Protection) check:
    val auditReport = SecurityEnforcer.enforce(applicationContext)
    if (auditReport.isCompromised) {
      SecurityEnforcer.clearEncryptedStorage(applicationContext)
    } else {
      // 0.1s Zero-Plaintext Encrypted Local Cache Boot
      com.example.data.AegoraRepository.initEncryptedCache(applicationContext)
    }

    setContent {
      AegoraTheme {
        ZeroDayAppSecurityContainer(
          lifecycleOwner = LocalLifecycleOwner.current,
          context = applicationContext
        ) {
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

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    IntentFirewall.validateAndSanitize(this, intent)
  }
}


