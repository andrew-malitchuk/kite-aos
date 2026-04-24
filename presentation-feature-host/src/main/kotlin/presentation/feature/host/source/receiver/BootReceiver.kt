package presentation.feature.host.source.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import presentation.feature.host.source.host.HostActivity

/**
 * BroadcastReceiver responsible for automatically starting the kiosk activity
 * when the device finishes booting.
 *
 * This ensures that the kiosk application remains active and persistent
 * across device restarts, which is a key requirement for kiosk-mode deployment.
 *
 * @see HostActivity
 * @since 0.0.1
 */
public class BootReceiver : BroadcastReceiver() {
    /**
     * Receives the [Intent.ACTION_BOOT_COMPLETED] broadcast and launches
     * the [HostActivity].
     *
     * Note: Special flags are used to ensure the activity is started even
     * with Android's background activity start restrictions.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(
                "BootReceiver",
                "Starting activity first for Android 14 background restrictions...",
            )

            val activityIntent =
                Intent(context, HostActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("AUTO_START", true)
                }
            context.startActivity(activityIntent)
        }
    }
}
