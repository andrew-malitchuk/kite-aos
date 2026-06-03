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

public class AutoRebootReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduler: AutoRebootScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AutoRebootScheduler.ACTION_AUTO_REBOOT -> handleReboot(context)
            Intent.ACTION_BOOT_COMPLETED -> scheduler.start()
        }
    }

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
