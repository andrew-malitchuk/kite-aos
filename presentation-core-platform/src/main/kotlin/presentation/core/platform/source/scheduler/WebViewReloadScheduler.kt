package presentation.core.platform.source.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import domain.core.source.model.ResilienceModel
import domain.usecase.api.source.usecase.configuration.ObserveResilienceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.Calendar

/**
 * Schedules the daily WebView reload that keeps a long-running dashboard from accumulating
 * renderer memory until it is killed.
 *
 * A `WebView`/`GeckoView` rendering a live dashboard for weeks accumulates GPU texture and JS heap;
 * the end state is a renderer kill (blank white page) or an OOM. Reloading resets that cost.
 *
 * **The alarm must come from [AlarmManager], never from a coroutine `delay` or a `Handler`.** In
 * Doze, in-process timers do not fire — and the overnight idle window is precisely when the reload
 * is both most needed and least disruptive. A timer-based implementation looks fine in testing and
 * then silently never runs in production.
 *
 * @param context Application [Context] used to obtain [AlarmManager].
 * @param observeResilienceUseCase Emits the current schedule so the alarm is re-armed on change.
 * @see presentation.core.platform.source.receiver.WebViewReloadReceiver
 * @since 2.2.0
 */
@Single
public class WebViewReloadScheduler(
    private val context: Context,
    private val observeResilienceUseCase: ObserveResilienceUseCase,
) {
    // SupervisorJob: a failure in one child coroutine does not cancel the parent or siblings.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Starts observing the reload schedule and re-arms the alarm on every change.
     *
     * Call once from `Application.onCreate`, and again from the boot-completed receiver: alarms do
     * not survive a reboot.
     *
     * @since 2.2.0
     */
    public fun start() {
        scope.launch {
            observeResilienceUseCase().collect { model ->
                rescheduleIfEnabled(model)
            }
        }
    }

    /**
     * Arms the alarm when the daily reload is enabled, or cancels it otherwise.
     *
     * @param model Current settings, or `null` to cancel unconditionally.
     * @since 2.2.0
     */
    public fun rescheduleIfEnabled(model: ResilienceModel?) {
        if (model?.isScheduledReloadOn == true) {
            schedule(model.reloadHourOrDefault)
        } else {
            cancel()
        }
    }

    /**
     * Schedules a one-shot exact alarm for the next occurrence of [hour].
     *
     * The alarm is one-shot and re-armed by the receiver after each firing, rather than being a
     * repeating alarm: computing the *next future* occurrence every time is what keeps the schedule
     * correct across a DST shift, where a fixed 24-hour period would drift by an hour.
     *
     * @param hour Hour of day (0–23) at which to reload.
     * @since 2.2.0
     */
    public fun schedule(hour: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancel()
        val triggerAt = nextOccurrenceMillis(hour)
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, buildPendingIntent())
            Log.d(TAG, "Daily WebView reload scheduled for hour=$hour")
        } catch (e: SecurityException) {
            Log.w(TAG, "Cannot schedule exact alarm: ${e.message}")
        }
    }

    /**
     * Cancels any pending reload alarm. Safe to call when none is scheduled.
     *
     * @since 2.2.0
     */
    public fun cancel() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent())
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(ACTION_SCHEDULED_RELOAD).apply { setPackage(context.packageName) }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextOccurrenceMillis(hour: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis
    }

    /**
     * Constants for the scheduled reload.
     *
     * @since 2.2.0
     */
    public companion object {
        private const val TAG = "WebViewReloadScheduler"

        // 0xRELOAD is not valid hex; 0xAE10A is a unique, arbitrary request code for this alarm.
        private const val REQUEST_CODE = 0xAE10A

        /**
         * Broadcast action sent by [AlarmManager] when the daily reload is due.
         *
         * Received by [presentation.core.platform.source.receiver.WebViewReloadReceiver].
         */
        public const val ACTION_SCHEDULED_RELOAD: String = "presentation.core.platform.ACTION_SCHEDULED_RELOAD"
    }
}
