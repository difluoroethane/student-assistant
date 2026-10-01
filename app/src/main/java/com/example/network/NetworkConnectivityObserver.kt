package com.example.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

interface ConnectivityObserver {
    fun observe(): Flow<NetworkStatus>
    fun getCurrentStatus(): NetworkStatus

    enum class NetworkStatus(val label: String, val isOnline: Boolean) {
        AvailableWifi("ONLINE • WI-FI", true),
        AvailableCellular("MOBILE DATA", true),
        Lost("OFFLINE MODE", false)
    }
}

class NetworkConnectivityObserver(
    private val context: Context
) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun getCurrentStatus(): ConnectivityObserver.NetworkStatus {
        val activeNetwork = connectivityManager.activeNetwork ?: return ConnectivityObserver.NetworkStatus.Lost
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return ConnectivityObserver.NetworkStatus.Lost
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityObserver.NetworkStatus.AvailableWifi
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityObserver.NetworkStatus.AvailableCellular
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> ConnectivityObserver.NetworkStatus.AvailableWifi
            else -> ConnectivityObserver.NetworkStatus.Lost
        }
    }

    override fun observe(): Flow<ConnectivityObserver.NetworkStatus> {
        return callbackFlow {
            // Immediately send the current status
            trySend(getCurrentStatus())

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val caps = connectivityManager.getNetworkCapabilities(network)
                    val status = resolveStatus(caps)
                    trySend(status)
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val status = resolveStatus(networkCapabilities)
                    trySend(status)
                }

                override fun onLost(network: Network) {
                    trySend(ConnectivityObserver.NetworkStatus.Lost)
                }

                override fun onUnavailable() {
                    trySend(ConnectivityObserver.NetworkStatus.Lost)
                }

                private fun resolveStatus(caps: NetworkCapabilities?): ConnectivityObserver.NetworkStatus {
                    if (caps == null || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        return ConnectivityObserver.NetworkStatus.Lost
                    }
                    return when {
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityObserver.NetworkStatus.AvailableWifi
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityObserver.NetworkStatus.AvailableCellular
                        else -> ConnectivityObserver.NetworkStatus.AvailableWifi
                    }
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            try {
                connectivityManager.registerNetworkCallback(request, callback)
            } catch (e: Exception) {
                try {
                    connectivityManager.registerDefaultNetworkCallback(callback)
                } catch (ignored: Exception) {}
            }

            awaitClose {
                try {
                    connectivityManager.unregisterNetworkCallback(callback)
                } catch (ignored: Exception) {}
            }
        }.distinctUntilChanged()
    }
}
