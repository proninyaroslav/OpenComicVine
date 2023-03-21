package org.proninyaroslav.opencomicvine.model.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.proninyaroslav.opencomicvine.di.ApplicationScope
import javax.inject.Inject

interface AppConnectivityManager {
    val observeNetworkAvailability: SharedFlow<Boolean>

    fun isNetworkAvailable(): Boolean
}

class AppConnectivityManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : AppConnectivityManager {
    private val connectivityManager: ConnectivityManager =
        getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager

    private val networkAvailability = MutableSharedFlow<Boolean>(replay = 1)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            coroutineScope.launch {
                networkAvailability.emit(true)
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            coroutineScope.launch {
                networkAvailability.emit(false)
            }
        }
    }

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
        .build()

    init {
        connectivityManager.requestNetwork(networkRequest, networkCallback)

        coroutineScope.launch {
            networkAvailability.emit(isNetworkAvailable())
        }
    }

    override val observeNetworkAvailability: SharedFlow<Boolean> = networkAvailability

    @Suppress("DEPRECATION")
    override fun isNetworkAvailable(): Boolean {
        var result = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI,
                        ConnectivityManager.TYPE_MOBILE,
                        ConnectivityManager.TYPE_VPN,
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }

        return result
    }
}