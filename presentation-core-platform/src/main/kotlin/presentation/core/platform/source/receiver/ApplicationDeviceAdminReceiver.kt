package presentation.core.platform.source.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Device administrator receiver for the application.
 *
 * This class handles events related to device administration privileges, which are required
 * for advanced kiosk features such as programmatic screen locking via
 * [android.app.admin.DevicePolicyManager.lockNow] and disabling the keyguard.
 *
 * The receiver must be declared in the `AndroidManifest.xml` with the
 * `android.permission.BIND_DEVICE_ADMIN` permission and a `device-admin` metadata resource
 * that lists the policies used by the application.
 *
 * @see presentation.core.platform.core.helper.DevicePowerManager
 * @see DeviceAdminReceiver
 * @since 0.0.1
 */
public class ApplicationDeviceAdminReceiver : DeviceAdminReceiver() {
    private companion object {
        private const val TAG = "DeviceAdmin"
    }

    /**
     * Called when the application is granted device administrator privileges.
     *
     * @param context The receiver's [Context].
     * @param intent The [Intent] that triggered this callback.
     * @see DeviceAdminReceiver.onEnabled
     * @since 0.0.1
     */
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Device administrator privileges granted.")
    }

    /**
     * Called when the application's device administrator privileges are revoked.
     *
     * Once revoked, features that depend on admin rights (e.g., [DevicePowerManager.lockDevice])
     * will no longer function.
     *
     * @param context The receiver's [Context].
     * @param intent The [Intent] that triggered this callback.
     * @see presentation.core.platform.core.helper.DevicePowerManager.lockDevice
     * @since 0.0.1
     */
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.w(TAG, "Device administrator privileges revoked.")
    }

    /**
     * Called when the user has requested to disable the device administrator.
     *
     * Returning a non-null [CharSequence] from the super implementation shows a warning dialog
     * to the user before the admin is actually disabled.
     *
     * @param context The receiver's [Context].
     * @param intent The [Intent] that triggered this callback.
     * @return A warning message to display to the user, or `null` for no warning.
     * @see DeviceAdminReceiver.onDisableRequested
     * @since 0.0.1
     */
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        Log.d(TAG, "Device administrator disable requested.")
        return super.onDisableRequested(context, intent)
    }
}
