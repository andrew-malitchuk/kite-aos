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
 * Helper class for managing device power states, including brightness and locking.
 */
@Suppress("MagicNumber")
public class DevicePowerManager(private val context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val adminComponent = ComponentName(context, ApplicationDeviceAdminReceiver::class.java)
    private val contentResolver: ContentResolver = context.contentResolver

    public companion object {
        private const val TAG = "DevicePowerManager"
        public const val BRIGHTNESS_DIM: Int = 10
        public const val BRIGHTNESS_MAX: Int = 255
    }

    /**
     * Updates the system screen brightness.
     * Requires [Settings.System.canWrite] permission.
     *
     * @param value The brightness value (0-255).
     * @return true if the brightness was updated, false otherwise.
     */
    @Suppress("MagicNumber")
    public fun setBrightness(value: Int): Boolean {
        return try {
            if (Settings.System.canWrite(context)) {
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
     * Wakes up the screen if it is currently off.
     */
    public fun wakeUp() {
        try {
            if (!powerManager.isInteractive) {
                val wakeLock =
                    powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "Yahk:MotionWakeLock",
                    )
                wakeLock.acquire(1000L)
                Log.i(TAG, "Screen wake-up triggered")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to wake up screen", e)
        }
    }

    /**
     * Programmatically locks the device.
     * Requires the app to be a Device Administrator.
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
