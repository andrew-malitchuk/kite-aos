package presentation.core.platform.core.helper

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import presentation.core.platform.source.receiver.ApplicationDeviceAdminReceiver

/**
 * Helper class for managing device power states, including screen brightness, wake-up, and
 * programmatic device locking.
 *
 * This manager is typically owned by [presentation.core.platform.source.service.MotionService]
 * and orchestrates screen state transitions in response to motion detection events.
 *
 * @param context The application or service [Context] used to obtain system services.
 * @see presentation.core.platform.source.service.MotionService
 * @see ApplicationDeviceAdminReceiver
 * @since 0.0.1
 */
// Suppress MagicNumber at class level because brightness constants (10, 255) are domain-specific
// values defined in the companion object, not arbitrary literals.
@Suppress("MagicNumber")
public class DevicePowerManager(private val context: Context) {
    // Unsafe cast is safe here — POWER_SERVICE always returns a PowerManager instance.
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    // Unsafe cast is safe here — DEVICE_POLICY_SERVICE always returns a DevicePolicyManager instance.
    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val adminComponent = ComponentName(context, ApplicationDeviceAdminReceiver::class.java)
    private val contentResolver: ContentResolver = context.contentResolver

    /**
     * Constants used by [DevicePowerManager].
     *
     * @since 0.0.1
     */
    public companion object {
        private const val TAG = "DevicePowerManager"

        /**
         * Minimum brightness level used when the screen should be dimmed but still visible.
         *
         * The value `10` keeps the backlight barely on, reducing power consumption while
         * providing visual feedback that the device is active.
         *
         * @since 0.0.1
         */
        public const val BRIGHTNESS_DIM: Int = 10

        /**
         * Maximum brightness level supported by the Android system brightness setting.
         *
         * Android's [Settings.System.SCREEN_BRIGHTNESS] uses a 0–255 integer range.
         *
         * @since 0.0.1
         */
        public const val BRIGHTNESS_MAX: Int = 255
    }

    /**
     * Updates the system screen brightness to the specified [value].
     *
     * This method requires the [Settings.System.canWrite] permission to be granted.
     * It performs a no-op if the current brightness already matches [value] to avoid
     * unnecessary system settings writes.
     *
     * @param value The brightness value in the range 0–255, where 0 is fully off and 255 is
     * maximum brightness.
     * @return `true` if the brightness was successfully updated, `false` if the permission is
     * missing, the value is unchanged, or an error occurred.
     * @see BRIGHTNESS_DIM
     * @see BRIGHTNESS_MAX
     * @since 0.0.1
     */
    // Suppress MagicNumber — the -1 sentinel in getInt is the Android default-value convention.
    @Suppress("MagicNumber")
    public fun setBrightness(value: Int): Boolean {
        return try {
            if (Settings.System.canWrite(context)) {
                // Read the current brightness to avoid redundant writes.
                val current = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, -1)
                if (current != value) {
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, value)
                    Log.i(TAG, "Brightness updated to $value")
                    return true
                }
            } else {
                Log.w(TAG, "Cannot write settings. Permission missing.")
            }
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set brightness", e)
            false
        }
    }

    /**
     * Wakes up the device screen if it is currently off or in a non-interactive state.
     *
     * Acquires a partial [PowerManager.SCREEN_BRIGHT_WAKE_LOCK] with
     * [PowerManager.ACQUIRE_CAUSES_WAKEUP] for 1 second, which is sufficient to turn the
     * screen on and hand control back to the system.
     *
     * @see PowerManager.isInteractive
     * @since 0.0.1
     */
    public fun wakeUp() {
        try {
            if (!powerManager.isInteractive) {
                val wakeLock =
                    powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "Yahk:MotionWakeLock",
                    )
                // Hold the wake lock for 1 second — just long enough to turn the screen on.
                wakeLock.acquire(1000L)
                Log.i(TAG, "Screen wake-up triggered")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to wake up screen", e)
        }
    }

    /**
     * Programmatically locks the device using the [DevicePolicyManager].
     *
     * This method requires the application to be registered as a Device Administrator via
     * [ApplicationDeviceAdminReceiver]. If the admin privilege has not been granted, the lock
     * request is silently skipped with a warning log.
     *
     * @see ApplicationDeviceAdminReceiver
     * @see DevicePolicyManager.lockNow
     * @since 0.0.1
     */
    public fun lockDevice() {
        try {
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                devicePolicyManager.lockNow()
                Log.i(TAG, "Device lock triggered")
            } else {
                Log.w(TAG, "Cannot lock device. Not a Device Administrator.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to lock device", e)
        }
    }
}
