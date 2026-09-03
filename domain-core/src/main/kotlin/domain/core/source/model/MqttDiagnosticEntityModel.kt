package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model enumerating the optional Home Assistant diagnostic entities the kiosk can publish.
 *
 * These entities describe the panel itself rather than anything it controls, so each one is
 * individually opt-out: a single-panel install has little use for a fleet-management version
 * sensor, while a multi-panel deployment depends on it. All are enabled by default.
 *
 * The [id] is both the MQTT topic segment and the Home Assistant `unique_id` suffix
 * (`{clientId}_{id}`), which is why it must stay stable — changing it would orphan the previously
 * registered entity in Home Assistant rather than rename it.
 *
 * @property id Stable topic / `unique_id` segment for this entity.
 * @see MqttModel
 * @see Model
 * @since 2.1.0
 */
public enum class MqttDiagnosticEntityModel(public val id: String) : Model {
    /** Seconds since the device last booted; a drop reveals a silent reboot. */
    Uptime("uptime"),

    /** Installed app `versionName`, for auditing a fleet of panels from one dashboard. */
    AppVersion("app_version"),

    /** Current LAN IPv4 address, so a DHCP lease change does not lose the panel. */
    IpAddress("ip_address"),

    /** Device-wide memory usage percentage; early warning before a renderer kill. */
    RamUsage("ram_usage"),
}
