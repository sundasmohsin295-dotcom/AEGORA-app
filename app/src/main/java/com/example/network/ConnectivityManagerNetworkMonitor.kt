package com.example.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Implementation of [NetworkMonitor] leveraging Android's [ConnectivityManager]
 * and Kotlin coroutines [callbackFlow] to deliver real-time network status changes.
 */
class ConnectivityManagerNetworkMonitor(
  private val context: Context
) : NetworkMonitor {

  private val connectivityManager: ConnectivityManager? =
    context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

  override val networkStatus: Flow<NetworkStatus> = callbackFlow {
    val cm = connectivityManager
    if (cm == null) {
      trySend(NetworkStatus.Offline)
      close()
      return@callbackFlow
    }

    // Emit initial status synchronously upon collection
    val initialStatus = getCurrentNetworkStatus(cm)
    trySend(initialStatus)

    val callback = object : ConnectivityManager.NetworkCallback() {
      override fun onAvailable(network: Network) {
        val status = getNetworkStatusForNetwork(cm, network)
        trySend(status)
      }

      override fun onLost(network: Network) {
        val status = getCurrentNetworkStatus(cm)
        trySend(status)
      }

      override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
      ) {
        val status = parseCapabilities(networkCapabilities)
        trySend(status)
      }

      override fun onUnavailable() {
        trySend(NetworkStatus.Offline)
      }
    }

    val request = NetworkRequest.Builder()
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build()

    try {
      cm.registerNetworkCallback(request, callback)
    } catch (e: Exception) {
      trySend(getCurrentNetworkStatus(cm))
    }

    awaitClose {
      try {
        cm.unregisterNetworkCallback(callback)
      } catch (e: Exception) {
        // Ignored if already unregistered
      }
    }
  }
    .distinctUntilChanged()
    .conflate()

  override val isOnline: Flow<Boolean> = networkStatus
    .map { it is NetworkStatus.Online }
    .distinctUntilChanged()
    .conflate()

  private fun getCurrentNetworkStatus(cm: ConnectivityManager): NetworkStatus {
    val activeNetwork = cm.activeNetwork ?: return NetworkStatus.Offline
    val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return NetworkStatus.Offline
    return parseCapabilities(capabilities)
  }

  private fun getNetworkStatusForNetwork(cm: ConnectivityManager, network: Network): NetworkStatus {
    val capabilities = cm.getNetworkCapabilities(network) ?: return NetworkStatus.Connecting
    return parseCapabilities(capabilities)
  }

  private fun parseCapabilities(capabilities: NetworkCapabilities): NetworkStatus {
    val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    if (!hasInternet) {
      return NetworkStatus.Offline
    }

    if (!isValidated) {
      return NetworkStatus.Connecting
    }

    val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    val isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

    return NetworkStatus.Online(
      isWifi = isWifi,
      isCellular = isCellular,
      isMetered = isMetered
    )
  }
}
