package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing the MQTT broker configuration and credentials.
 *
 * @property enabled Whether MQTT telemetry is active.
 * @property ip The broker's IP address or hostname.
 * @property port The broker's connection port.
 * @property clientId Unique identifier for this device on the broker.
 * @property username Username for broker authentication.
 * @property password Password for broker authentication.
 * @property friendlyName Human-readable name used for device discovery (e.g., in Home Assistant).
 */
public data class MqttModel(
    val enabled: Boolean? = null,
    val ip: String? = null,
    val port: String? = null,
    val clientId: String? = null,
    val username: String? = null,
    val password: String? = null,
    val friendlyName: String? = null,
) : Model
