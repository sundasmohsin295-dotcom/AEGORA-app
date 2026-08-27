package com.example.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface for monitoring real-time network connectivity and capabilities across the application.
 */
interface NetworkMonitor {
  /**
   * Flow of boolean indicating whether the device currently has active, validated internet access.
   */
  val isOnline: Flow<Boolean>

  /**
   * Flow of detailed [NetworkStatus] specifying connection transport (WiFi, Cellular, Metered) and states.
   */
  val networkStatus: Flow<NetworkStatus>
}
