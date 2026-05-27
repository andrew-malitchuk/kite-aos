package data.platform.api.source.connectivity

import kotlinx.coroutines.flow.Flow

/**
 * Interface for observing real-time network connectivity changes on the Android platform.
 *
 * Implementations should provide a reactive stream of [NetworkStatus] updates,
 * allowing features to adapt their behavior based on internet availability.
 *
 * @see data.platform.impl.source.connectivity.ConnectivityObserverImpl
 * @since 0.0.1
 */
public interface ConnectivityObserver {
    /**
     * A [Flow] that emits the current [NetworkStatus] whenever it changes.
     *
     * Subscribing to this flow should ideally emit the current connectivity state
     * immediately upon collection.
     *
     * @return a [Flow] of [NetworkStatus] representing connectivity state transitions.
     */
    public val status: Flow<NetworkStatus>

    /**
     * Represents the possible states of network connectivity as seen by the system.
     *
     * @since 0.0.1
     */
    public enum class NetworkStatus {
        /** The device has an active internet connection. */
        Available,

        /** The current network connection is unstable and may be lost soon. */
        Losing,

        /** The previously available network connection has been lost. */
        Lost,

        /** No network connectivity is currently available. */
        Unavailable,
    }
}
