package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model carrying the low-frequency companion telemetry published to Home Assistant.
 *
 * These four values change slowly and are sampled together on a periodic tick rather than being
 * event-driven like battery or motion, so they travel as one model and are published in a single
 * pass. Each value maps to its own Home Assistant sensor
 * (see [MqttDiagnosticEntityModel]); grouping happens on the sampling side only, not on the wire,
 * which keeps every entity's payload a plain scalar with no template to render.
 *
 * @property uptimeSeconds Seconds since the device last booted.
 * @property appVersion Installed app `versionName`, or an empty string when unavailable.
 * @property ipAddress Current LAN IPv4 address, or an empty string when the device is offline.
 * @property ramUsagePercent Device-wide memory usage as a percentage (0–100).
 *
 * @see MqttDiagnosticEntityModel
 * @see Model
 * @since 2.1.0
 */
public data class CompanionTelemetryModel(
    val uptimeSeconds: Long,
    val appVersion: String,
    val ipAddress: String,
    val ramUsagePercent: Int,
) : Model
