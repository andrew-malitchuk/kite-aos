package presentation.core.application

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import androidx.core.content.ContextCompat
import presentation.core.application.di.appModule
import presentation.core.platform.source.receiver.BatteryReceiver
import presentation.core.platform.source.service.MqttService

/**
 * The main [Application] class for the Yahk project.
 *
 * This class serves as the entry point for the application lifecycle and is responsible for:
 * 1. Initializing the Koin dependency injection framework.
 * 2. Registering global system receivers (e.g., [BatteryReceiver]).
 * 3. Bootstrapping critical background services like [MqttService].
 */
public class YahkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@YahkApplication)
            modules(appModule)
        }

        registerReceiver(BatteryReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // Start MQTT Service as Foreground
        val intent = Intent(this, MqttService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}