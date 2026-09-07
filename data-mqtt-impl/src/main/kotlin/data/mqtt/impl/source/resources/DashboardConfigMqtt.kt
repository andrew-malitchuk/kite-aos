package data.mqtt.impl.source.resources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration payload for registering a dashboard-reachability binary sensor with Home Assistant
 * via MQTT Discovery.
 *
 * Serialized to JSON and published to `homeassistant/binary_sensor/<clientId>_dashboard/config` as
 * a retained message. The state topic reports whether the panel can currently reach the dashboard
 * backend.
 *
 * **This is deliberately a separate entity rather than the device availability topic.** Device
 * availability answers "is the panel running?"; this answers "can the panel see Home Assistant?".
 * Folding the second into the first would mark every entity on the device unavailable during a
 * Home Assistant restart — including the panel's own controls, which are working fine — and would
 * make the panel look dead exactly when someone is trying to diagnose the server.
 *
 * Payloads are literal `online` / `offline` strings with no `value_template`, matching every other
 * entity here and sidestepping the Jinja capitalisation trap described in the module guide.
 *
 * Example payload:
 * ```json
 * {
 *   "device": { "identifiers": ["kite_abc123"], "manufacturer": "Kite Kiosk", "name": "Living Room Tablet" },
 *   "device_class": "connectivity",
 *   "name": "Dashboard",
 *   "state_topic": "kite_abc123_dashboard/dashboard/state",
 *   "payload_on": "online",
 *   "payload_off": "offline",
 *   "availability_topic": "kite_abc123/availability",
 *   "entity_category": "diagnostic",
 *   "unique_id": "kite_abc123_dashboard"
 * }
 * ```
 *
 * @property device The device information this sensor belongs to.
 * @property deviceClass The Home Assistant device class (default: `"connectivity"`).
 * @property name Human-readable name shown in Home Assistant.
 * @property stateTopic MQTT topic where the reachability state is published.
 * @property payloadOn Payload representing a reachable backend (default: `"online"`).
 * @property payloadOff Payload representing an unreachable backend (default: `"offline"`).
 * @property availabilityTopic MQTT topic carrying the device-wide `online`/`offline` payload.
 * @property entityCategory Home Assistant entity category; `"diagnostic"` keeps it off the
 *   device's primary control card.
 * @property uniqueId Unique identifier for the entity, must be stable across restarts.
 *
 * @see DeviceMqtt
 * @since 2.2.0
 */
@Serializable
internal data class DashboardConfigMqtt(
    @SerialName("device")
    val device: DeviceMqtt,
    @SerialName("device_class")
    val deviceClass: String = "connectivity",
    @SerialName("name")
    val name: String = "Dashboard",
    @SerialName("state_topic")
    val stateTopic: String,
    @SerialName("payload_on")
    val payloadOn: String = "online",
    @SerialName("payload_off")
    val payloadOff: String = "offline",
    @SerialName("availability_topic")
    val availabilityTopic: String,
    @SerialName("entity_category")
    val entityCategory: String = "diagnostic",
    @SerialName("unique_id")
    val uniqueId: String,
)
