package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.audio.CyberSonificationManager
import com.example.hunter.AutonomousThreatHunterScheduler
import com.example.security.BehavioralBiometricEngine
import com.example.security.KernelWatchdog
import com.example.ui.components.GlobalErrorBoundary
import com.example.ui.screens.DuelArenaScreen
import com.example.viewmodel.DuelArenaViewModel

class MainActivity : ComponentActivity() {

  private val duelViewModel: DuelArenaViewModel by viewModels()
  private var activeDeepLinkSessionId by mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Autonomous Threat Hunter WorkManager & Self-Healing Kernel Watchdog (Phase 33)
    AutonomousThreatHunterScheduler.schedulePeriodicHunter(this)
    KernelWatchdog.startMonitoring(this)

    handleDeepLink(intent?.data)

    setContent {
      com.example.ui.theme.ObsidianIndustrialTheme {
        GlobalErrorBoundary(modifier = Modifier.fillMaxSize()) {
          DuelArenaScreen(
            viewModel = duelViewModel,
            deepLinkSessionId = activeDeepLinkSessionId,
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleDeepLink(intent.data)
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    ev?.let {
      if (it.action == MotionEvent.ACTION_UP || it.action == MotionEvent.ACTION_DOWN) {
        val duration = (it.eventTime - it.downTime).coerceAtLeast(10L)
        BehavioralBiometricEngine.recordTouchInteraction(it.pressure, duration)
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  private fun handleDeepLink(data: Uri?) {
    if (data == null) return
    Log.i("MainActivity", "Deep link intercepted: $data")
    if (data.scheme == "aegora" && data.host == "claim-web-pro") {
      val sessionId = data.getQueryParameter("session_id") ?: "sess_web_pro_claimed"
      activeDeepLinkSessionId = sessionId
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    CyberSonificationManager.getInstance(this).release()
  }
}
