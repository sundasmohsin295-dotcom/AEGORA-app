package com.example.network

/**
 * Represents the granular real-time network connectivity state of the device.
 */
sealed interface NetworkStatus {
  /**
   * The device has validated internet connectivity.
   *
   * @property isWifi True if connected via Wi-Fi transport.
   * @property isCellular True if connected via cellular data transport.
   * @property isMetered True if network is metered (e.g. cellular limit).
   */
  data class Online(
    val isWifi: Boolean = false,
    val isCellular: Boolean = false,
    val isMetered: Boolean = false
  ) : NetworkStatus

  /**
   * The device is completely offline or has lost internet connectivity.
   */
  data object Offline : NetworkStatus

  /**
   * Network connection is transiently being established or validated.
   */
  data object Connecting : NetworkStatus
}
