# Module: data-mqtt-impl

## Overview
This module provides a concrete implementation of the MQTT API using the **kmqtt** library. It includes advanced features like automatic reconnection and Home Assistant MQTT Discovery.

## Responsibilities
*   **MQTT Client Management**: Implements connection logic using `kmqtt`, including a background reconnection loop and state management.
*   **Home Assistant Integration**: Automatically registers the device and its sensors (motion binary sensor, battery sensor) in Home Assistant using MQTT Discovery topics.
*   **Telemetry Publishing**: Handles the actual transmission of telemetry data to the broker with appropriate topics and payloads.
*   **Dependency Injection**: Configures Koin modules for providing the `TelemetryMqttSource` implementation.

## Architecture
This module implements `TelemetryMqttSource` using a background `Job` for connection management and `kotlinx-serialization` for discovery payloads.

### Key Components
*   **`data.mqtt.impl.source.datasource.TelemetryMqttSourceImpl`**: The core implementation managing the `MQTTClient` lifecycle and publishing logic.
*   **`data.mqtt.impl.source.resources.*`**: Internal DTOs used for Home Assistant discovery configuration (Device, Battery, Motion).
*   **`data.mqtt.impl.di.DataMqttImplModule`**: Koin module for providing the MQTT source.

### Form-Factor Entity Gating (`@since 1.2.0`, Android TV)
`connect(...)` now receives a `model` parameter (`"tv"`/`"tablet"`) that is emitted in every discovery `device` block via the new `DeviceMqtt.model` field, so Home Assistant shows the device's form-factor.

When `model == "tv"`, the implementation adapts because several controls are not meaningful or reliably controllable on Android TV (no battery; audio owned by the TV/receiver over HDMI/ARC; backlight not exposed via `Settings.System`; screen locking needs a Device Administrator that Android TV lacks):
*   **Skipped registrations**: the battery, volume, brightness and screen entities are not registered on TV. Motion, URL, FAB, screensaver and camera-URL entities are always registered on both form-factors.
*   **Skipped command subscriptions**: `subscribeToCommandTopics(...)` omits the `volume/set`, `brightness/set` and `screen/set` topics on TV.
*   **Stale-registration cleanup**: `unregisterEntity(...)` publishes an empty retained payload to the config topics for the brightness, screen, volume and battery entities, clearing any registration left over from a previous mobile (tablet) form-factor so those entities disappear from Home Assistant.

## Dependencies
*   **`data-mqtt-api`**: Implements the contracts from this module.
*   **`kmqtt`**: Multiplatform MQTT client library.
*   **`kotlinx-serialization`**: For JSON payload formatting.
