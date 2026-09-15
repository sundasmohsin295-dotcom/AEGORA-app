package com.example

import android.app.Application
import android.util.Log
import com.revenuecat.purchases.LogHandler
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class AegoraApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    // Initialize CommerceManager & RevenueCat SDK before UI loads
    try {
      com.example.commerce.CommerceManager.initialize(this)
    } catch (e: Throwable) {
      Log.w("AegoraApplication", "CommerceManager initialization notice: ${e.message}")
    }
  }
}
