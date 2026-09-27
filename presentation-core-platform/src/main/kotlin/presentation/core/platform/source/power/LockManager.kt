package presentation.core.platform.source.power

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import domain.usecase.api.source.usecase.configuration.ObserveResilienceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

/**
 * Sole owner of the two long-lived locks that keep a wall-mounted panel reachable while its screen
 * is off: a CPU wake lock and a WiFi lock.
 *
 * The failure this exists to prevent is silent. With the screen off, aggressive OEM power
 * management parks the WiFi radio and deprioritises the process; nothing crashes and no error is
 * logged, but the MQTT session dies and the dashboard shows stale data to whoever walks up to the
 * panel in the morning. Holding these locks keeps the radio and the process alive through the
 * screen-off window.
 *
 * The two locks are deliberately **not** governed by one setting. They solve different problems
 * and they fail differently:
 *
 * * The **CPU lock** is held unconditionally for as long as the kiosk process lives. A
 *   `PARTIAL_WAKE_LOCK` does not touch the display, costs almost nothing on a mains-powered panel,
 *   and has no known failure mode worth an escape hatch.
 * * The **WiFi lock** is user-disableable, and that toggle ships with the feature rather than
 *   after the first bug report. On some budget WiFi chipsets — which is to say, on exactly the
 *   cheap tablets people mount on walls — holding the radio out of power-save for hours wedges the
 *   driver: WiFi drops and then refuses to reassociate, often reporting a bogus authentication
 *   failure, until the radio is fully reset. Without a way to turn this off, the feature would
 *   trade one class of silent overnight failure for another.
 *
 * **What the WiFi lock actually does, per API level.** Below API 29 `WIFI_MODE_FULL_HIGH_PERF`
 * disables radio power-save outright. From API 29 the platform claims to keep WiFi associated on
 * its own and `WIFI_MODE_FULL` became a no-op, leaving `WIFI_MODE_FULL_LOW_LATENCY` as the only
 * mode with an effect — and it degrades to the old full-lock behaviour once the app is no longer
 * foreground, which is precisely the screen-off window this class cares about. So the lock is
 * requested on every supported release, but it does more work on older hardware.
 *
 * Locks are acquired for the lifetime of the process and are released by the framework when the
 * process dies, so there is no teardown call here. The one release path that matters is the user
 * switching the WiFi lock off, which [start] handles by reacting to the preference.
 *
 * @param context Application [Context] used to reach `PowerManager` and `WifiManager`.
 * @param observeResilienceUseCase Source of the user's WiFi-lock opt-out.
 * @see presentation.core.platform.core.helper.DevicePowerManager
 * @since 2.2.0
 */
@Single
public class LockManager(
    private val context: Context,
    private val observeResilienceUseCase: ObserveResilienceUseCase,
) {
    // SupervisorJob: a failure in one child coroutine does not cancel the parent or siblings.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Unsafe cast is safe here — POWER_SERVICE always returns a PowerManager instance.
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    // The application context specifically: a WifiManager obtained from an Activity or Service
    // context outlives it and leaks it.
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // Reference counting off on both locks: acquisition is idempotent, so a duplicate acquire
    // cannot leave a counter that a single release fails to unwind.
    private val cpuLock: PowerManager.WakeLock by lazy {
        powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CPU_LOCK_TAG).apply {
            setReferenceCounted(false)
        }
    }

    private val wifiLock: WifiManager.WifiLock by lazy {
        wifiManager.createWifiLock(wifiLockMode, WIFI_LOCK_TAG).apply {
            setReferenceCounted(false)
        }
    }

    private val wifiLockMode: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiManager.WIFI_MODE_FULL_LOW_LATENCY
        } else {
            // Deprecated from API 29, but below it this is the mode that actually disables radio
            // power-save, and it is only ever requested there.
            @Suppress("DEPRECATION")
            WifiManager.WIFI_MODE_FULL_HIGH_PERF
        }

    /**
     * Acquires the CPU lock and starts tracking the user's WiFi-lock setting.
     *
     * Call once from `Application.onCreate`.
     *
     * @since 2.2.0
     */
    public fun start() {
        acquireCpuLock()
        scope.launch {
            observeResilienceUseCase().collect { model ->
                applyWifiLock(isEnabled = model?.isWifiLockOn ?: true)
            }
        }
    }

    // No timeout by design: a timeout would expire mid-night and hand the process back to the
    // power manager during the exact window the lock exists to cover.
    @SuppressLint("WakelockTimeout")
    private fun acquireCpuLock() {
        try {
            if (!cpuLock.isHeld) {
                cpuLock.acquire()
                Log.i(TAG, "CPU wake lock acquired")
            }
        } catch (exception: RuntimeException) {
            // A device that refuses the lock still runs the kiosk — it just sleeps more deeply.
            Log.e(TAG, "Failed to acquire CPU wake lock", exception)
        }
    }

    private fun applyWifiLock(isEnabled: Boolean) {
        try {
            when {
                isEnabled && !wifiLock.isHeld -> {
                    wifiLock.acquire()
                    Log.i(TAG, "WiFi lock acquired (mode=$wifiLockMode)")
                }

                !isEnabled && wifiLock.isHeld -> {
                    wifiLock.release()
                    Log.i(TAG, "WiFi lock released at user request")
                }
            }
        } catch (exception: RuntimeException) {
            Log.e(TAG, "Failed to update WiFi lock", exception)
        }
    }

    private companion object {
        private const val TAG = "LockManager"

        // Tags are surfaced in `dumpsys power` / battery attribution, so they name the owner.
        private const val CPU_LOCK_TAG = "Yahk:KioskCpuLock"
        private const val WIFI_LOCK_TAG = "Yahk:KioskWifiLock"
    }
}
