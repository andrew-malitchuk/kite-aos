package presentation.core.platform.source.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import presentation.core.platform.source.diagnostics.CrashRelaunchHandler

/**
 * [BroadcastReceiver] that brings the kiosk back after a fatal crash.
 *
 * Fired by the one-shot alarm [CrashRelaunchHandler] arms on its way down. Because the alarm is
 * delivered to a *fresh* process, this runs with no dependency on anything that was alive when the
 * crash happened.
 *
 * The launch intent is resolved from [android.content.pm.PackageManager] rather than by naming the
 * host Activity directly: `presentation-core-platform` sits below the feature modules in the
 * dependency graph and cannot reference `HostActivity`. The leanback fallback covers Android TV,
 * where the app has no regular `CATEGORY_LAUNCHER` entry.
 *
 * @see CrashRelaunchHandler
 * @since 2.2.0
 */
public class KioskRelaunchReceiver : BroadcastReceiver() {

    /**
     * Relaunches the kiosk when the relaunch alarm fires.
     *
     * @param context The [Context] in which the receiver is running.
     * @param intent The [Intent] being received; only [CrashRelaunchHandler.ACTION_RELAUNCH_KIOSK]
     *   is acted on.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != CrashRelaunchHandler.ACTION_RELAUNCH_KIOSK) return

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: context.packageManager.getLeanbackLaunchIntentForPackage(context.packageName)

        if (launchIntent == null) {
            Log.w(TAG, "No launch intent for ${context.packageName}; cannot relaunch")
            return
        }

        // NEW_TASK is required when starting an Activity from a receiver context. CLEAR_TOP drops
        // any stale task the dead process may have left behind, so the kiosk comes back at its
        // entry point rather than resuming a half-initialised screen.
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            context.startActivity(launchIntent)
            Log.i(TAG, "Kiosk relaunched after crash")
        } catch (e: SecurityException) {
            // Background activity starts are restricted on newer Android versions; an alarm-driven
            // start is normally permitted, but log rather than crash the fresh process if not.
            Log.e(TAG, "Relaunch blocked", e)
        }
    }

    private companion object {
        private const val TAG = "KioskRelaunchReceiver"
    }
}
