package presentation.core.platform.source.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import domain.core.source.model.AutoRebootModel
import domain.usecase.api.source.usecase.configuration.ObserveAutoRebootUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.Calendar

/**
 * Schedules and manages an exact wake-up alarm for the auto-reboot feature.
 *
 * Uses [AlarmManager.setExactAndAllowWhileIdle] so the alarm fires reliably even in Doze mode.
 * The scheduler subscribes to [ObserveAutoRebootUseCase] and automatically reschedules the alarm
 * whenever the user updates the auto-reboot configuration.
 *
 * The actual reboot is performed by [presentation.core.platform.source.receiver.AutoRebootReceiver]
 * when it receives the [ACTION_AUTO_REBOOT] broadcast.
 *
 * @param context The application [Context] used to obtain system services.
 * @param observeAutoRebootUseCase Flow-based use case that emits updated [AutoRebootModel] values.
 * @see presentation.core.platform.source.receiver.AutoRebootReceiver
 * @since 0.0.5
 */
@Single
public class AutoRebootScheduler(
    private val context: Context,
    private val observeAutoRebootUseCase: ObserveAutoRebootUseCase,
) {
    // SupervisorJob: a failure in one child coroutine does not cancel the parent or siblings.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * Starts observing the auto-reboot configuration and reschedules the alarm on each update.
     *
     * Intended to be called from [presentation.core.platform.source.receiver.AutoRebootReceiver.onReceive]
     * on [android.content.Intent.ACTION_BOOT_COMPLETED] to survive device reboots.
     */
    public fun start() {
        scope.launch {
            observeAutoRebootUseCase().collect { model ->
                rescheduleIfEnabled(model)
            }
        }
    }

    /**
     * Schedules a one-shot exact alarm that will trigger the auto-reboot at the next valid time.
     *
     * If the configured time has already passed today, the alarm is deferred by [AutoRebootModel.intervalDays].
     * Any previously scheduled alarm is cancelled before the new one is set.
     *
     * @param model The auto-reboot configuration specifying hour, minute, and interval.
     */
    public fun schedule(model: AutoRebootModel) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancel()

        val hour = model.hour ?: 3
        val minute = model.minute ?: 0
        val intervalDays = model.intervalDays ?: 1

        val triggerAtMillis = nextOccurrenceMillis(hour, minute, intervalDays)
        val pendingIntent = buildPendingIntent()

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
            Log.d("AutoRebootScheduler", "Alarm scheduled: hour=$hour, minute=$minute, intervalDays=$intervalDays")
        } catch (e: SecurityException) {
            Log.w("AutoRebootScheduler", "Cannot schedule exact alarm: ${e.message}")
        }
    }

    /**
     * Cancels any pending auto-reboot alarm.
     *
     * Safe to call even if no alarm is currently scheduled.
     */
    public fun cancel() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent())
        Log.d("AutoRebootScheduler", "Alarm cancelled")
    }

    /**
     * Schedules the alarm when [AutoRebootModel.enabled] is `true`, or cancels it otherwise.
     *
     * @param model The auto-reboot configuration, or `null` to cancel unconditionally.
     */
    public fun rescheduleIfEnabled(model: AutoRebootModel?) {
        if (model?.enabled == true) {
            schedule(model)
        } else {
            cancel()
        }
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(ACTION_AUTO_REBOOT).apply {
            setPackage(context.packageName)
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextOccurrenceMillis(hour: Int, minute: Int, intervalDays: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, intervalDays)
        }
        return target.timeInMillis
    }

    public companion object {
        /**
         * Broadcast action sent by [AlarmManager] when the auto-reboot time is reached.
         *
         * Received by [presentation.core.platform.source.receiver.AutoRebootReceiver].
         */
        public const val ACTION_AUTO_REBOOT: String = "presentation.core.platform.ACTION_AUTO_REBOOT"

        // 0xAB00B is a unique, arbitrary request code used to identify this alarm's PendingIntent.
        private const val REQUEST_CODE = 0xAB00B
    }
}
