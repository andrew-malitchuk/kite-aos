package presentation.core.platform.source.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.core.platform.source.command.RemoteCommandBus
import presentation.core.platform.source.scheduler.WebViewReloadScheduler

/**
 * [BroadcastReceiver] for the daily WebView reload alarm and for re-arming it after a reboot.
 *
 * Handles two actions:
 * - [WebViewReloadScheduler.ACTION_SCHEDULED_RELOAD] — raises a reload on [RemoteCommandBus] and
 *   immediately re-arms the next occurrence. The alarm is one-shot by design, so re-arming here is
 *   what keeps the schedule alive, and recomputing the next occurrence each time keeps it correct
 *   across a DST shift.
 * - [Intent.ACTION_BOOT_COMPLETED] — restarts the scheduler, because alarms are cleared on reboot.
 *
 * @see WebViewReloadScheduler
 * @since 2.2.0
 */
public class WebViewReloadReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduler: WebViewReloadScheduler by inject()
    private val remoteCommandBus: RemoteCommandBus by inject()

    /**
     * Routes the received broadcast to the appropriate handler.
     *
     * @param context The [Context] in which the receiver is running.
     * @param intent The [Intent] being received.
     */
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WebViewReloadScheduler.ACTION_SCHEDULED_RELOAD -> {
                Log.i(TAG, "Scheduled dashboard reload fired")
                remoteCommandBus.emitReload()
                // Re-arm from the observed settings; start() re-collects and schedules the next one.
                scheduler.start()
            }

            Intent.ACTION_BOOT_COMPLETED -> scheduler.start()
        }
    }

    private companion object {
        private const val TAG = "WebViewReloadReceiver"
    }
}
