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

## Dependencies
*   **`data-core`**: For base resource types.
