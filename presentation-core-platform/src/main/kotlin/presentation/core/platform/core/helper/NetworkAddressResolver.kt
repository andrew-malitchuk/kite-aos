package presentation.core.platform.core.helper

import java.net.NetworkInterface

/**
 * Resolves the device's local WiFi IPv4 address.
 *
 * Iterates [NetworkInterface] entries and returns the first non-loopback, non-link-local
 * site-local IPv4 address. This is the address reachable from other devices on the LAN.
 *
 * @since 0.1.0
 */
public object NetworkAddressResolver {

    /**
     * Returns the device's LAN IPv4 address, or `null` if not connected to a network.
     *
     * Filters out loopback, link-local, and IPv6 addresses. Returns only the first
     * matching site-local address, which is the address reachable by other devices on the LAN.
     *
     * @return The IPv4 address string (e.g., `"192.168.1.100"`), or `null` when no suitable
     * interface is active or an exception occurs.
     * @since 0.1.0
     */
    public fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces()
                ?.asSequence()
                ?.filter { it.isUp && !it.isLoopback }
                ?.flatMap { it.inetAddresses.asSequence() }
                ?.firstOrNull { address ->
                    !address.isLoopbackAddress &&
                        !address.isLinkLocalAddress &&
                        address.isSiteLocalAddress &&
                        address.hostAddress?.contains(':') == false // exclude IPv6
                }
                ?.hostAddress
        } catch (_: Exception) {
            null
        }
    }
}
