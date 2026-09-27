package presentation.core.application

import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Process
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import presentation.core.application.BuildConfig
import presentation.core.application.di.appModule
import presentation.core.platform.source.config.AppConfig
import presentation.core.platform.source.connection.DashboardConnectionMonitor
import presentation.core.platform.source.diagnostics.CrashRelaunchHandler
import presentation.core.platform.source.diagnostics.CrashRelaunchSettingMirror
import presentation.core.platform.source.diagnostics.MemoryRecoveryCoordinator
import presentation.core.platform.source.power.LockManager
import presentation.core.platform.source.receiver.BatteryReceiver
import presentation.core.platform.source.scheduler.AutoRebootScheduler
import presentation.core.platform.source.scheduler.WebViewReloadScheduler
import presentation.feature.main.source.webview.engine.preWarmGeckoRuntime

/**
 * The main [Application] class for the Yahk project.
 *
 * This class serves as the entry point for the application lifecycle and is responsible for:
 * 1. Initialising the Koin dependency injection framework via [appModule].
 * 2. Registering global system receivers (e.g., [BatteryReceiver] for battery-level telemetry).
 *
 * @see appModule
 * @see BatteryReceiver
 * @since 0.0.1
 */
public class YahkApplication : Application() {

    private val memoryRecoveryCoordinator: MemoryRecoveryCoordinator by inject()

    /**
     * Called when the application is first created.
     *
     * Performs two sequential bootstrap steps:
     * 1. Starts the Koin DI container with all application modules.
     * 2. Registers the [BatteryReceiver] for `ACTION_BATTERY_CHANGED` broadcasts.
     *
     * @see Application.onCreate
     * @since 0.0.1
     */
    override fun onCreate() {
        super.onCreate()

        // GeckoView spawns child processes that also trigger Application.onCreate().
        // Skip full initialization in those processes — only the main process needs DI, receivers, and services.
        if (!isMainProcess()) return

        // Seed the compile-time form-factor flag before Koin is started so AppConfig
        // can fall back to it when runtime detection (leanback / UI mode) is unavailable.
        AppConfig.buildFlagIsTv = BuildConfig.IS_TV

        // GeckoView 147+ requires API 26 (lutimes syscall); gms builds support API 25 and
        // use Android WebView instead, so pre-warming GeckoRuntime there causes a fatal
        // dlopen failure on armeabi-v7a API-25 devices.
        // NOTE: with the second `formfactor` dimension, BuildConfig.FLAVOR is the combined
        // name (e.g. "fossMobile"); use the per-dimension field to test distribution alone.
        if (BuildConfig.FLAVOR_distribution == "foss") {
            preWarmGeckoRuntime(applicationContext)
        }

        // Initialise the Koin dependency injection container with logging and the Android context.
        startKoin {
            androidLogger()
            androidContext(this@YahkApplication)
            modules(appModule)
        }

        // Register the battery broadcast receiver to monitor charge level changes system-wide.
        // Skipped on Android TV: TV boxes have no battery, so the sensor would only report
        // meaningless values (and its HA entity is not registered on TV).
        val appConfig: AppConfig by inject()
        if (!appConfig.isTv) {
            registerReceiver(BatteryReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }

        // Start observing auto reboot config and schedule the alarm accordingly.
        val autoRebootScheduler: AutoRebootScheduler by inject()
        autoRebootScheduler.start()

        CrashlyticsInitializer.init()

        // Install the crash handler *after* Crashlytics, so the previous default handler this
        // chains to is Crashlytics' own and fatal crashes are still reported upstream.
        val crashRelaunchHandler: CrashRelaunchHandler by inject()
        crashRelaunchHandler.install()

        // Keeps the crash-relaunch setting readable synchronously from the dying process.
        val crashRelaunchSettingMirror: CrashRelaunchSettingMirror by inject()
        crashRelaunchSettingMirror.start()

        // Daily dashboard reload, armed via AlarmManager so it still fires in Doze.
        val webViewReloadScheduler: WebViewReloadScheduler by inject()
        webViewReloadScheduler.start()

        memoryRecoveryCoordinator.start()

        // Pauses the WebView while the dashboard backend is unreachable.
        val dashboardConnectionMonitor: DashboardConnectionMonitor by inject()
        dashboardConnectionMonitor.start()

        // Holds the CPU and WiFi locks that keep the panel reachable while its screen is off.
        val lockManager: LockManager by inject()
        lockManager.start()
    }

    /**
     * Turns system memory pressure into a deferred dashboard reload.
     *
     * A `WebView`/`GeckoView` rendering a live dashboard for weeks accumulates GPU texture and JS
     * heap until the renderer is killed and the panel shows a blank page. Reloading resets that,
     * but only once the screensaver is up, so the reload is never visible to someone standing at
     * the panel — see [MemoryRecoveryCoordinator].
     *
     * @param level The trim level reported by the system.
     * @since 2.2.0
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (!isMainProcess()) return
        // Only react to genuine pressure. The lighter UI-hidden levels fire on every backgrounding
        // and would schedule a pointless reload every time the panel idles.
        if (level >= TRIM_MEMORY_RUNNING_LOW) {
            memoryRecoveryCoordinator.onMemoryPressure()
        }
    }

    // Returns true only when the current process is the main app process, identified by packageName.
    // GeckoView spawns auxiliary processes whose Application.onCreate would otherwise re-run DI init.
    private fun isMainProcess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // API 28+: getProcessName() is a fast, direct system call.
            getProcessName() == packageName
        } else {
            // Pre-API 28: iterate running processes to find the one matching the current PID.
            val pid = Process.myPid()
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            manager.runningAppProcesses?.any { it.pid == pid && it.processName == packageName } ?: false
        }
    }
}
