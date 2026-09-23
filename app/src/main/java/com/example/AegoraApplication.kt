package com.example

import android.app.Application
import android.util.Log
import com.example.layers.LayersExperimentManager
import com.example.mesh.AegoraMeshSync
import com.example.onesignal.AegoraOneSignalManager
import com.example.security.BehavioralBiometricEngine
import com.example.security.MovingTargetDefenseEngine

class AegoraApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    Log.i("AegoraApp", "Aegora Enterprise App starting with Monadic error isolation...")

    // Initialize Layers SDK A/B Testing
    LayersExperimentManager.initialize(this)

    // Initialize OneSignal Push Notifications & Retention Tags
    AegoraOneSignalManager.initialize(this)
    AegoraOneSignalManager.notifySkillDecayHigh()

    // Initialize Phase 35 & 36 Bastion Security Subsystems
    BehavioralBiometricEngine.initialize(this)
    MovingTargetDefenseEngine.startMutationLoop()
    AegoraMeshSync.initialize(this)
  }
}
