package presentation.core.application

import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Process
import androidx.core.content.ContextCompat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import presentation.core.application.di.appModule
import presentation.core.platform.source.receiver.BatteryReceiver
import presentation.core.platform.source.service.MqttService
import presentation.feature.main.source.webview.engine.preWarmGeckoRuntime

/**
 * The main [Application] class for the Yahk project.
 *
 * This class serves as the entry point for the application lifecycle and is responsible for:
 * 1. Initialising the Koin dependency injection framework via [appModule].
 * 2. Registering global system receivers (e.g., [BatteryReceiver] for battery-level telemetry).
 * 3. Bootstrapping critical background services such as [MqttService].
 *
 * @see appModule
 * @see BatteryReceiver
 * @see MqttService
 * @since 0.0.1
 */
public class YahkApplication : Application() {

    /**
     * Called when the application is first created.
     *
     * Performs three sequential bootstrap steps:
     * 1. Starts the Koin DI container with all application modules.
     * 2. Registers the [BatteryReceiver] for `ACTION_BATTERY_CHANGED` broadcasts.
     * 3. Launches the [MqttService] as a foreground service to maintain an MQTT connection.
     *
     * @see Application.onCreate
     * @since 0.0.1
     */
    override fun onCreate() {
        super.onCreate()

        // GeckoView spawns child processes that also trigger Application.onCreate().
        // Skip full initialization in those processes — only the main process needs DI, receivers, and services.
        if (!isMainProcess()) return

        preWarmGeckoRuntime(applicationContext)

        // Initialise the Koin dependency injection container with logging and the Android context.
        startKoin {
            androidLogger()
            androidContext(this@YahkApplication)
            modules(appModule)
        }

        // Register the battery broadcast receiver to monitor charge level changes system-wide.
        registerReceiver(BatteryReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // Start the MQTT foreground service so that telemetry stays alive independently of the UI.
        val intent = Intent(this, MqttService::class.java)
        ContextCompat.startForegroundService(this, intent)

        CrashlyticsInitializer.init()
    }

    private fun isMainProcess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getProcessName() == packageName
        } else {
            val pid = Process.myPid()
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            manager.runningAppProcesses?.any { it.pid == pid && it.processName == packageName } ?: false
        }
    }
}
