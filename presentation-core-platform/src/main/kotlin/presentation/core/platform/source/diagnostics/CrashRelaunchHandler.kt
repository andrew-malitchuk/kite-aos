package presentation.core.platform.source.diagnostics

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import org.koin.core.annotation.Single

/**
 * [Thread.UncaughtExceptionHandler] that schedules the kiosk to relaunch itself after a fatal crash.
 *
 * Nothing else brings the app back after a crash: `START_STICKY` restarts a *service*, not the
 * Activity, so an unattended panel drops to the launcher and stays there until somebody physically
 * touches it — which on a wall mount with no visible launcher can mean days.
 *
 * The relaunch is scheduled through [AlarmManager], which fires from **outside** the dying process.
 * That is what makes this work with no overlay permission, no device-owner privilege, and no
 * surviving service.
 *
 * The previous default handler is always invoked afterwards, so Firebase Crashlytics (on the `gms`
 * flavor) still receives the crash and the platform still produces its normal tombstone.
 *
 * @param context Application context used for [AlarmManager] and the relaunch broadcast.
 * @param store Crash-safe storage backing the report log and the relaunch rate limiter.
 * @see CrashDiagnosticsStore
 * @see KioskRelaunchReceiver
 * @since 2.2.0
 */
@Single
public class CrashRelaunchHandler(
    private val context: Context,
    private val store: CrashDiagnosticsStore,
) : Thread.UncaughtExceptionHandler {

    private var previousHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * Installs this handler as the process-wide default, chaining to whatever was installed before.
     *
     * Safe to call once during [android.app.Application.onCreate]. Calling it again would chain the
     * handler to itself, so repeat calls are ignored.
     *
     * @since 2.2.0
     */
    public fun install() {
        if (previousHandler != null) return
        previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        Log.i(TAG, "Crash relaunch handler installed")
    }

    /**
     * Records the crash, schedules a relaunch when permitted, then delegates to the previous handler.
     *
     * @param t The thread that raised the exception.
     * @param e The fatal exception.
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            // The report is written even when relaunching is switched off — a panel that keeps
            // dying is exactly the case where the user needs to be able to read why.
            val allowRelaunch = store.recordCrashAndAllowRelaunch(e)
            when {
                !store.isRelaunchEnabled -> Log.i(TAG, "Crash relaunch disabled by user; not rescheduling")
                !allowRelaunch -> {
                    val windowSeconds = CrashDiagnosticsStore.CRASH_WINDOW_MS / MILLIS_PER_SECOND
                    Log.w(
                        TAG,
                        "Crash rate limit reached " +
                            "(${CrashDiagnosticsStore.MAX_CRASHES_IN_WINDOW} in ${windowSeconds}s); " +
                            "letting the device come to rest",
                    )
                }
                else -> scheduleRelaunch()
            }
        } catch (handlerFailure: Exception) {
            // Never let bookkeeping mask the original crash.
            Log.e(TAG, "Failed to handle crash", handlerFailure)
        } finally {
            // Chain unconditionally so Crashlytics and the platform still see the original crash.
            previousHandler?.uncaughtException(t, e)
        }
    }

    /** Arms a one-shot alarm that re-opens the kiosk shortly after the process dies. */
    private fun scheduleRelaunch() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = System.currentTimeMillis() + RELAUNCH_DELAY_MS
        try {
            // setExactAndAllowWhileIdle: the panel may be idle overnight when it crashes, and an
            // inexact alarm could be deferred by Doze for a long time.
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, buildPendingIntent())
            Log.i(TAG, "Relaunch scheduled in ${RELAUNCH_DELAY_MS}ms")
        } catch (e: SecurityException) {
            Log.w(TAG, "Cannot schedule exact alarm for relaunch: ${e.message}")
        }
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(ACTION_RELAUNCH_KIOSK).apply { setPackage(context.packageName) }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /**
     * Constants for the crash relaunch path.
     *
     * @since 2.2.0
     */
    public companion object {
        private const val TAG = "CrashRelaunchHandler"

        /** Divisor used to render the rate-limiter window in seconds for the log line. */
        private const val MILLIS_PER_SECOND = 1000L

        // 0xC2A54 ("crash") is a unique, arbitrary request code for this alarm's PendingIntent.
        private const val REQUEST_CODE = 0xC2A54

        /**
         * Delay before the kiosk is brought back, in milliseconds.
         *
         * Long enough for the crashing process to be fully torn down — relaunching into a
         * half-dead process tends to fail — and short enough that a passer-by sees a brief blink
         * rather than a dead panel.
         */
        private const val RELAUNCH_DELAY_MS = 5_000L

        /**
         * Broadcast action sent by [AlarmManager] when the kiosk should be relaunched.
         *
         * Received by [KioskRelaunchReceiver].
         */
        public const val ACTION_RELAUNCH_KIOSK: String = "presentation.core.platform.ACTION_RELAUNCH_KIOSK"
    }
}
