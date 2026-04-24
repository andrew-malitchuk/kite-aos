package presentation.core.platform.source.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import domain.usecase.api.source.usecase.mqtt.MqttSendBatteryLevelUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * [BroadcastReceiver] that monitors system battery changes and reports the current battery
 * percentage to the telemetry system via MQTT.
 *
 * This receiver listens for [Intent.ACTION_BATTERY_CHANGED] broadcasts, computes the battery
 * level as an integer percentage, and delegates publishing to [MqttSendBatteryLevelUseCase].
 *
 * Because [onReceive] must complete quickly, the MQTT send is performed asynchronously using
 * [goAsync] combined with a coroutine on [Dispatchers.IO].
 *
 * @see MqttSendBatteryLevelUseCase
 * @see presentation.core.platform.source.service.MqttService
 * @since 0.0.1
 */
public class BatteryReceiver : BroadcastReceiver(), KoinComponent {
    private val mqttSendBatteryLevelUseCase: MqttSendBatteryLevelUseCase by inject()

    // SupervisorJob ensures that a failure in one coroutine does not cancel sibling launches.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private companion object {
        private const val TAG = "BatteryReceiver"
    }

    /**
     * Called when an [Intent.ACTION_BATTERY_CHANGED] broadcast is received.
     *
     * Extracts the battery level and scale extras, calculates the percentage, and sends it
     * to the MQTT broker asynchronously. The [goAsync] pending result is used to keep the
     * receiver alive until the coroutine completes.
     *
     * @param context The [Context] in which the receiver is running.
     * @param intent The [Intent] being received, expected to carry [BatteryManager.EXTRA_LEVEL]
     * and [BatteryManager.EXTRA_SCALE] extras.
     * @see MqttSendBatteryLevelUseCase
     * @since 0.0.1
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            // -1 is used as a sentinel to detect missing extras.
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level != -1 && scale > 0) {
                // Calculate battery percentage: (level / scale) * 100, truncated to an integer.
                val batteryPct = (level * 100 / scale.toFloat()).toInt()
                Log.d(TAG, "Battery level changed: $batteryPct%")

                // goAsync() returns a PendingResult that keeps the receiver alive past onReceive().
                val pendingResult = goAsync()
                scope.launch {
                    try {
                        mqttSendBatteryLevelUseCase(batteryPct)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send battery level to MQTT", e)
                    } finally {
                        // Signal the system that async work is done so the receiver can be recycled.
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
