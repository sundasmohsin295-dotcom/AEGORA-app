package com.example.network

import android.content.Context
import com.example.data.AegoraRepository
import com.example.model.NetworkSyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * NetworkMonitorService provides a singleton implementation of [NetworkMonitor]
 * using Android's ConnectivityManager and reactive Kotlin Flows.
 * It automatically reflects real-time connectivity status in the global application state [AegoraRepository].
 */
object NetworkMonitorService : NetworkMonitor {
  private val _isConnected = MutableStateFlow(true)
  val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

  private val _currentStatus = MutableStateFlow<NetworkStatus>(NetworkStatus.Online(isWifi = true))
  override val networkStatus: Flow<NetworkStatus> = _currentStatus.asStateFlow()

  override val isOnline: Flow<Boolean> = _isConnected.asStateFlow()

  private var delegateMonitor: ConnectivityManagerNetworkMonitor? = null
  private val scope = CoroutineScope(Dispatchers.Default + Job())
  private var syncJob: Job? = null
  private var isInitialized = false

  fun initialize(context: Context) {
    if (isInitialized) return
    isInitialized = true

    val monitor = ConnectivityManagerNetworkMonitor(context.applicationContext)
    delegateMonitor = monitor

    monitor.networkStatus
      .onEach { status ->
        _currentStatus.value = status
        val online = status is NetworkStatus.Online
        val wasConnected = _isConnected.value
        _isConnected.value = online

        if (online) {
          if (!wasConnected) {
            triggerSyncOnConnectionRestore()
          } else {
            AegoraRepository.setNetworkSyncStatus(NetworkSyncStatus.SYNCED)
          }
        } else if (status is NetworkStatus.Connecting) {
          AegoraRepository.setNetworkSyncStatus(NetworkSyncStatus.SYNCING)
        } else {
          syncJob?.cancel()
          AegoraRepository.setNetworkSyncStatus(NetworkSyncStatus.OFFLINE)
        }
      }
      .launchIn(scope)
  }

  private fun triggerSyncOnConnectionRestore() {
    syncJob?.cancel()
    syncJob = scope.launch {
      AegoraRepository.setNetworkSyncStatus(NetworkSyncStatus.SYNCING)
      // Smooth transient sync transition
      delay(1500)
      AegoraRepository.setNetworkSyncStatus(NetworkSyncStatus.SYNCED)
    }
  }
}
