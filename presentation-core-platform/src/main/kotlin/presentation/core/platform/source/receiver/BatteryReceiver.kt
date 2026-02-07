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
 * BroadcastReceiver that monitors system battery changes.
 *
 * It calculates the battery percentage and reports it to the telemetry system via MQTT.
 */
public class BatteryReceiver : BroadcastReceiver(), KoinComponent {

    private val mqttSendBatteryLevelUseCase: MqttSendBatteryLevelUseCase by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private companion object {
        private const val TAG = "BatteryReceiver"
    }

    /**
     * Called when the [Intent.ACTION_BATTERY_CHANGED] broadcast is received.
     * Extracts battery level and scale, calculates the percentage, and sends it via MQTT.
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level != -1 && scale > 0) {
                val batteryPct = (level * 100 / scale.toFloat()).toInt()
                Log.d(TAG, "Battery level changed: $batteryPct%")

                val pendingResult = goAsync()
                scope.launch {
                    try {
                        mqttSendBatteryLevelUseCase(batteryPct)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send battery level to MQTT", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
