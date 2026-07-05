package presentation.core.platform.source.receiver

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.core.platform.source.scheduler.AutoRebootScheduler

/**
 * [BroadcastReceiver] that handles scheduled auto-reboot and boot-completed events.
 *
 * Receives two distinct actions:
 * - [AutoRebootScheduler.ACTION_AUTO_REBOOT] — triggers a device reboot via
 *   [DevicePolicyManager.reboot] if the app is the device owner. If not, reschedules the next
 *   alarm and skips the reboot with a warning log.
 * - [Intent.ACTION_BOOT_COMPLETED] — calls [AutoRebootScheduler.start] to re-register the alarm
 *   after the device restarts (alarms are cleared on reboot).
 *
 * @see AutoRebootScheduler
 * @see ApplicationDeviceAdminReceiver
 * @since 0.0.5
 */
public class AutoRebootReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduler: AutoRebootScheduler by inject()

    /**
     * Routes the received broadcast to the appropriate handler.
     *
     * @param context The [Context] in which the receiver is running.
     * @param intent The [Intent] being received. Expected actions are
     *   [AutoRebootScheduler.ACTION_AUTO_REBOOT] and [Intent.ACTION_BOOT_COMPLETED].
     */
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AutoRebootScheduler.ACTION_AUTO_REBOOT -> handleReboot(context)
            Intent.ACTION_BOOT_COMPLETED -> scheduler.start()
        }
    }

    /**
     * Executes a programmatic reboot when the app is the device owner.
     *
     * Reschedules the alarm in both branches so the timer is always active after this call.
     *
     * @param context The [Context] used to obtain [DevicePolicyManager].
     */
    private fun handleReboot(context: Context) {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dpm.isDeviceOwnerApp(context.packageName)) {
            scheduler.start()
            val admin = ComponentName(
                context,
                ApplicationDeviceAdminReceiver::class.java,
            )
            dpm.reboot(admin)
        } else {
            Log.w("AutoRebootReceiver", "Device owner not set — cannot reboot programmatically")
            scheduler.start()
        }
    }
}
