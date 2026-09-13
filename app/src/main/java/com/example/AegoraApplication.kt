package com.example

import android.app.Application
import android.util.Log
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class AegoraApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    // Initialize RevenueCat SDK before UI loads using key from BuildConfig
    try {
      Purchases.logLevel = LogLevel.DEBUG
      val apiKey = BuildConfig.REVENUECAT_PUBLIC_API_KEY
      Purchases.configure(
        PurchasesConfiguration.Builder(this, apiKey).build()
      )
      Log.i("AegoraApplication", "RevenueCat SDK successfully configured with BuildConfig key.")
    } catch (e: Throwable) {
      Log.e("AegoraApplication", "RevenueCat SDK initialization notice: ${e.message}", e)
    }
  }
}
