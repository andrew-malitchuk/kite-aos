package presentation.core.platform.core.helper

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import domain.core.source.model.CompanionTelemetryModel

/**
 * Samples the low-frequency device facts published to Home Assistant as diagnostic sensors.
 *
 * All four values are cheap, permission-free reads, which is what makes it acceptable to poll them
 * on a timer rather than wire up four separate observers.
 *
 * @param context Application context used for package and memory lookups.
 * @see CompanionTelemetryModel
 * @see presentation.core.platform.source.service.MqttService
 * @since 2.1.0
 */
public class DeviceTelemetryProvider(private val context: Context) {

    private val activityManager: ActivityManager? =
        context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

    /**
     * The installed app version name, resolved once — it cannot change without the process being
     * restarted, so re-reading it on every tick would be pure waste.
     */
    private val appVersion: String by lazy { resolveAppVersion() }

    /**
     * Samples the current companion telemetry.
     *
     * @return A [CompanionTelemetryModel] snapshot. Fields that cannot be resolved come back as an
     *   empty string or `0` rather than throwing; the data layer skips publishing blanks so an
     *   unresolvable value never lands in Home Assistant as a bogus reading.
     * @since 2.1.0
     */
    public fun sample(): CompanionTelemetryModel = CompanionTelemetryModel(
        // elapsedRealtime() counts from boot and, unlike uptimeMillis(), keeps counting through
        // deep sleep — which is most of a wall panel's night.
        uptimeSeconds = SystemClock.elapsedRealtime() / MILLIS_PER_SECOND,
        appVersion = appVersion,
        ipAddress = NetworkAddressResolver.getLocalIpAddress() ?: "",
        ramUsagePercent = sampleRamUsagePercent(),
    )

    /**
     * Reads device-wide memory usage as a percentage.
     *
     * Device-wide rather than per-process on purpose: Android's low-memory killer decides what to
     * kill based on free system memory, so this is the number that actually predicts the blank-page
     * renderer kill a long-running dashboard hits.
     *
     * @return Usage percentage in `0..100`, or `0` when [ActivityManager] is unavailable.
     */
    private fun sampleRamUsagePercent(): Int {
        val manager = activityManager ?: return 0
        val memoryInfo = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(memoryInfo)
        val total = memoryInfo.totalMem
        // Guard against a zero total: the division below would otherwise throw on an emulator or
        // an OEM image that reports nothing useful here.
        if (total <= 0L) return 0
        val used = total - memoryInfo.availMem
        return (used * PERCENT / total).toInt().coerceIn(0, PERCENT.toInt())
    }

    /** Resolves the app `versionName`, or an empty string when the package cannot be read. */
    private fun resolveAppVersion(): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
    } catch (_: PackageManager.NameNotFoundException) {
        // Cannot happen for our own package in practice, but the API forces the branch.
        ""
    }

    private companion object {
        private const val MILLIS_PER_SECOND = 1000L
        private const val PERCENT = 100L
    }
}
