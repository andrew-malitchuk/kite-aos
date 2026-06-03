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

@Single
public class AutoRebootScheduler(
    private val context: Context,
    private val observeAutoRebootUseCase: ObserveAutoRebootUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    public fun start() {
        scope.launch {
            observeAutoRebootUseCase().collect { model ->
                rescheduleIfEnabled(model)
            }
        }
    }

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

    public fun cancel() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent())
        Log.d("AutoRebootScheduler", "Alarm cancelled")
    }

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
        public const val ACTION_AUTO_REBOOT: String = "presentation.core.platform.ACTION_AUTO_REBOOT"
        private const val REQUEST_CODE = 0xAB00B
    }
}
