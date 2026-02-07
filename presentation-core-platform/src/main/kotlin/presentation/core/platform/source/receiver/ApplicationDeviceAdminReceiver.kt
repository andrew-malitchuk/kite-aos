package presentation.core.platform.source.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

import android.util.Log

/**
 * Device administrator receiver for the application.
 *
 * This class handles events related to device administration privileges, which are required
 * for advanced kiosk features such as programmatic screen locking and disabling the keyguard.
 */
public class ApplicationDeviceAdminReceiver : DeviceAdminReceiver() {

    private companion object {
        private const val TAG = "DeviceAdmin"
    }

    /**
     * Called when the application is granted device administrator privileges.
     */
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Device administrator privileges granted.")
    }

    /**
     * Called when the application's device administrator privileges are revoked.
     */
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.w(TAG, "Device administrator privileges revoked.")
    }

    /**
     * Called when the user has requested to disable the device administrator.
     */
    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        Log.d(TAG, "Device administrator disable requested.")
        return super.onDisableRequested(context, intent)
    }
}