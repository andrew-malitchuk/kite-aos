package data.platform.impl.source.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import data.platform.api.source.connectivity.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.annotation.Single

/**
 * Android implementation of [ConnectivityObserver] using the system's [ConnectivityManager].
 *
 * This implementation utilizes [callbackFlow] to bridge the imperative [ConnectivityManager.NetworkCallback]
 * API with a reactive Kotlin Coroutines [Flow]. It provides robust monitoring of internet availability
 * by filtering for networks with the [NetworkCapabilities.NET_CAPABILITY_INTERNET] capability.
 *
 * @param context The Android [Context] used to obtain the [ConnectivityManager] system service.
 * @see ConnectivityObserver
 * @since 0.0.1
 */
@Single(binds = [ConnectivityObserver::class])
internal class ConnectivityObserverImpl(
    context: Context,
) : ConnectivityObserver {
    // Unsafe cast is safe here: CONNECTIVITY_SERVICE always returns ConnectivityManager on Android.
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * A [Flow] that emits updates to the network connectivity status.
     *
     * The flow logic:
     * 1. Immediately checks and emits the initial state using [isNetworkAvailable].
     * 2. Registers a [ConnectivityManager.NetworkCallback] to listen for real-time changes.
     * 3. Uses [distinctUntilChanged] to ensure consumers only receive actual status transitions.
     * 4. Automatically unregisters the callback when the flow collection is stopped (via [awaitClose]).
     *
     * @return a [Flow] of [ConnectivityObserver.NetworkStatus] reflecting real-time connectivity changes.
     */
    override val status: Flow<ConnectivityObserver.NetworkStatus> =
        callbackFlow {
            // Bridge the callback-based ConnectivityManager API into a coroutine channel.
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        trySend(ConnectivityObserver.NetworkStatus.Available)
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        trySend(ConnectivityObserver.NetworkStatus.Losing)
                    }

                    override fun onLost(network: Network) {
                        trySend(ConnectivityObserver.NetworkStatus.Lost)
                    }

                    override fun onUnavailable() {
                        trySend(ConnectivityObserver.NetworkStatus.Unavailable)
                    }
                }

            // Emit the current connectivity snapshot before registering the callback,
            // so collectors receive an immediate value without waiting for a network event.
            val initialState =
                if (isNetworkAvailable()) {
                    ConnectivityObserver.NetworkStatus.Available
                } else {
                    ConnectivityObserver.NetworkStatus.Unavailable
                }
            trySend(initialState)

            // Only monitor networks that claim internet capability.
            val request =
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            connectivityManager.registerNetworkCallback(request, callback)

            // Unregister the callback when the flow collector cancels, preventing leaks.
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()

    /**
     * Checks whether the device currently has an active network with internet capability.
     *
     * @return `true` if the active network has [NetworkCapabilities.NET_CAPABILITY_INTERNET],
     *         `false` otherwise or if no active network exists.
     */
    private fun isNetworkAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
