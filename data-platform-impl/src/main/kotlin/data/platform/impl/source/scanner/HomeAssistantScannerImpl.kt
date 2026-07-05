package data.platform.impl.source.scanner

import data.platform.api.source.resource.HomeAssistantHost
import data.platform.api.source.scanner.HomeAssistantScanner
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Single

/**
 * Android implementation of [HomeAssistantScanner].
 *
 * Discovery proceeds in two phases:
 * 1. **Quick probes** — tries well-known mDNS hostnames with a short 900 ms timeout.
 * 2. **Subnet scan** — derives the /24 prefix from the device's active IPv4 interface and
 *    probes all 254 host addresses in parallel (up to [SCAN_PARALLELISM] concurrent coroutines,
 *    [SCAN_HOST_TIMEOUT_MS] ms per host).
 *
 * The entire operation is capped at [SCAN_TOTAL_TIMEOUT_MS] ms. Detection heuristic:
 * an HTTP GET to `http://{host}:{port}/api/` returning 200, 401, or 403 is treated as
 * a Home Assistant endpoint.
 *
 * @since 0.0.5
 */
@Single(binds = [HomeAssistantScanner::class])
internal class HomeAssistantScannerImpl : HomeAssistantScanner {

    @Suppress("MagicNumber")
    override suspend fun discover(): List<HomeAssistantHost> {
        return withTimeoutOrNull(SCAN_TOTAL_TIMEOUT_MS) {
            coroutineScope {
                val quickResults = QUICK_HOSTS.map { host ->
                    async {
                        probeHost(host, QUICK_PROBE_TIMEOUT_MS)
                            ?.let { HomeAssistantHost(url = it, source = "quick") }
                    }
                }.awaitAll().filterNotNull()

                val subnetPrefix = getSubnetPrefix()
                val scanResults = if (subnetPrefix != null) {
                    val semaphore = Semaphore(SCAN_PARALLELISM)
                    (1..254).map { i ->
                        async {
                            semaphore.withPermit {
                                probeHost("$subnetPrefix.$i", SCAN_HOST_TIMEOUT_MS)
                                    ?.let { HomeAssistantHost(url = it, source = "scan") }
                            }
                        }
                    }.awaitAll().filterNotNull()
                } else {
                    emptyList()
                }

                (quickResults + scanResults).distinctBy { it.url }
            }
        } ?: emptyList()
    }

    /**
     * Probes a single [host] for a Home Assistant HTTP endpoint on each port in [HA_PORTS].
     *
     * An HTTP response of 200, 401, or 403 from `http://{host}:{port}/api/` is treated as
     * a positive identification of a Home Assistant instance.
     *
     * @param host The hostname or IPv4 address to probe.
     * @param timeoutMs The connect and read timeout in milliseconds per port attempt.
     * @return The base URL `http://{host}:{port}` if the host is identified, or `null` if
     *   all port probes fail or time out.
     */
    private suspend fun probeHost(host: String, timeoutMs: Int): String? =
        withContext(Dispatchers.IO) {
            for (port in HA_PORTS) {
                try {
                    val connection = URL("http://$host:$port/api/").openConnection() as HttpURLConnection
                    connection.connectTimeout = timeoutMs
                    connection.readTimeout = timeoutMs
                    connection.requestMethod = "GET"
                    connection.instanceFollowRedirects = false
                    try {
                        connection.connect()
                        val code = connection.responseCode
                        if (code == HTTP_OK || code == HTTP_UNAUTHORIZED || code == HTTP_FORBIDDEN) {
                            return@withContext "http://$host:$port"
                        }
                    } finally {
                        connection.disconnect()
                    }
                } catch (_: Exception) {
                    // Host not reachable on this port — try the next one.
                }
            }
            null
        }

    /**
     * Derives the /24 subnet prefix (e.g., `"192.168.1"`) from the device's active IPv4 interface.
     *
     * Iterates over all active, non-loopback network interfaces and returns the three-octet
     * prefix of the first IPv4 address found. Returns `null` if no suitable interface exists
     * or if a security exception is thrown.
     *
     * @return The subnet prefix string (e.g., `"192.168.1"`), or `null` if undetermined.
     */
    private fun getSubnetPrefix(): String? =
        try {
            NetworkInterface.getNetworkInterfaces()
                ?.toList()
                ?.filter { it.isUp && !it.isLoopback }
                ?.flatMap { it.interfaceAddresses }
                ?.firstOrNull { it.address is Inet4Address && !it.address.isLoopbackAddress }
                ?.let { ifAddr ->
                    val ip = (ifAddr.address as? Inet4Address)?.hostAddress ?: return null
                    val parts = ip.split(".")
                    if (parts.size == 4) "${parts[0]}.${parts[1]}.${parts[2]}" else null
                }
        } catch (_: Exception) {
            null
        }

    private companion object {
        /** mDNS hostnames tried during the quick-probe phase before the full subnet scan. */
        val QUICK_HOSTS = listOf("homeassistant.local", "hass.local")

        /** TCP ports probed on each host; 8123 is the default HA port, 80 is the HTTP fallback. */
        val HA_PORTS = listOf(8123, 80)

        /** Connect/read timeout in milliseconds for mDNS quick probes. */
        const val QUICK_PROBE_TIMEOUT_MS = 900

        /** Connect/read timeout in milliseconds for each host during the subnet scan. */
        const val SCAN_HOST_TIMEOUT_MS = 650

        /** Maximum number of concurrent host probes during the subnet scan. */
        const val SCAN_PARALLELISM = 40

        /** Overall timeout in milliseconds for the entire [discover] operation. */
        const val SCAN_TOTAL_TIMEOUT_MS = 12_000L

        /** HTTP 200 OK — the HA API endpoint responded successfully. */
        const val HTTP_OK = 200

        /** HTTP 401 Unauthorized — the HA API endpoint requires authentication (still a valid HA response). */
        const val HTTP_UNAUTHORIZED = 401

        /** HTTP 403 Forbidden — the HA API endpoint rejected the request (still a valid HA response). */
        const val HTTP_FORBIDDEN = 403
    }
}
