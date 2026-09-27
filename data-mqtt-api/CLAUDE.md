# Module: data-mqtt-api

## Overview
This module defines the API layer for MQTT communication, focusing on telemetry and device state reporting. It provides the contracts necessary for the application to interact with an MQTT broker.

## Responsibilities
*   **Contract Definition**: Defines the `TelemetryMqttSource` interface for managing MQTT connections and reporting device telemetry (e.g., motion, battery).

## Architecture
This module follows the Clean Architecture approach by decoupling MQTT requirements from any specific MQTT client implementation.

### Key Components
*   **`data.mqtt.api.source.datasource.TelemetryMqttSource`**: The primary interface for connecting to a broker, disconnecting, and sending telemetry data such as motion detection events and battery levels.

### Form-Factor Aware Connection (`@since 1.2.0`)
`TelemetryMqttSource.connect(...)` takes a `model: String` parameter (`"tv"` or `"tablet"`) describing the device form-factor. It is reported to Home Assistant in the discovery `device` block and lets the implementation tailor which entities are registered — on Android TV the controls that cannot be driven (battery, volume, brightness, screen on/off) are omitted. See `data-mqtt-impl` for the gating behavior.

### Diagnostic Opt-Outs (`@since 2.1.0`)
`connect(...)` gained a `diagnostics: Set<String>` parameter carrying plain topic ids (`"uptime"`, `"app_version"`, `"ip_address"`, `"ram_usage"`). The set is deliberately `String` rather than a domain enum: the data layer does not depend on `domain-core`, so the translation from `MqttDiagnosticEntityModel` happens one layer up in `MqttRepositoryImpl`. Ids *absent* from the set are actively unregistered on connect — merely not publishing them would leave the entity in Home Assistant frozen at its last value, which reads as a stale sensor rather than a disabled one.

### Companion Telemetry (`@since 2.1.0`)
`sendCompanionTelemetry(uptimeSeconds, appVersion, ipAddress, ramUsagePercent)` publishes the low-frequency device-health values, each to its own state topic as a plain scalar. Values whose entity was not requested at connect time are skipped, so a disabled diagnostic costs no traffic. Blank `appVersion`/`ipAddress` are skipped too — publishing an empty string would show as a blank sensor rather than an absent one.

### Dashboard Reachability (`@since 2.2.0`)
`sendDashboardState(isReachable)` reports whether the dashboard backend is answering, as its own entity rather than through device availability. During a Home Assistant restart the panel itself is perfectly healthy; marking the whole device unavailable would be factually wrong and would hide the panel's own working controls (brightness, volume, screen) from the very automations that might need them to recover.

### Lifecycle & Discovery Teardown (`@since 2.1.0`)
*   `disconnect()` publishes a retained `offline` availability payload **before** sending DISCONNECT. A graceful disconnect makes the broker discard the Last Will, so without this explicit publish Home Assistant would keep the panel marked available after it deliberately went away.
*   `purgeDiscovery()` publishes an empty retained payload to every registered entity's `config` topic — the MQTT Discovery idiom for deletion. It must run while still connected, and is what separates "MQTT turned off" from "MQTT dropped out": a plain `disconnect()` leaves the retained configs on the broker, so the entities would linger in Home Assistant forever and need manual removal.

### Remote Command Channel (`@since 2.2.0`)
`observeCommands()` now emits two shapes of inbound message. The per-entity command topics (volume, brightness, screen, app launch, FAB, screensaver, clear cache, motion) carry a bare value as before. The shared `{clientId}/command/set` topic instead carries a JSON envelope `{ "action": "...", "value": "..." }`, so a single HA `text`/`button` entity or a raw `mqtt.publish` call can drive navigation, reload, back/forward, cache clearing and JS evaluation without one discovery entity per verb. Collectors route by topic.

## Dependencies
*   **`data-core`**: For base resource types.
