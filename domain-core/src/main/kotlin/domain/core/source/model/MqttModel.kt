package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing the MQTT broker configuration and credentials.
 *
 * Alongside the connection details this model carries the per-entity opt-out flags for the
 * optional Home Assistant diagnostic entities. Each flag is nullable and treated as **enabled**
 * when absent, so a config saved before those entities existed keeps publishing them rather than
 * silently losing them on upgrade.
 *
 * @property enabled Whether MQTT telemetry is active.
 * @property ip The broker's IP address or hostname.
 * @property port The broker's connection port.
 * @property clientId Unique identifier for this device on the broker.
 * @property username Username for broker authentication.
 * @property password Password for broker authentication.
 * @property friendlyName Human-readable name used for device discovery (e.g., in Home Assistant).
 * @property uptimeEnabled Whether the `uptime` diagnostic sensor is published (`null` = enabled).
 * @property appVersionEnabled Whether the `app_version` diagnostic sensor is published (`null` = enabled).
 * @property ipAddressEnabled Whether the `ip_address` diagnostic sensor is published (`null` = enabled).
 * @property ramUsageEnabled Whether the `ram_usage` diagnostic sensor is published (`null` = enabled).
 *
 * @see MqttDiagnosticEntityModel
 * @see Model
 * @since 0.0.1
 */
public data class MqttModel(
    val enabled: Boolean? = null,
    val ip: String? = null,
    val port: String? = null,
    val clientId: String? = null,
    val username: String? = null,
    val password: String? = null,
    val friendlyName: String? = null,
    val uptimeEnabled: Boolean? = null,
    val appVersionEnabled: Boolean? = null,
    val ipAddressEnabled: Boolean? = null,
    val ramUsageEnabled: Boolean? = null,
) : Model {

    /**
     * The set of diagnostic entities this configuration wants published.
     *
     * Resolves the nullable per-entity flags into a concrete set, defaulting each absent flag to
     * enabled. Entities missing from the set are actively unregistered from Home Assistant on the
     * next connection rather than merely left unpublished, so opting one out removes it from the
     * dashboard instead of leaving it behind as an entity stuck at its last value.
     *
     * @since 2.1.0
     */
    public val enabledDiagnostics: Set<MqttDiagnosticEntityModel>
        get() = buildSet {
            if (uptimeEnabled != false) add(MqttDiagnosticEntityModel.Uptime)
            if (appVersionEnabled != false) add(MqttDiagnosticEntityModel.AppVersion)
            if (ipAddressEnabled != false) add(MqttDiagnosticEntityModel.IpAddress)
            if (ramUsageEnabled != false) add(MqttDiagnosticEntityModel.RamUsage)
        }
}
