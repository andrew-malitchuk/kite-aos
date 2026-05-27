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

## Dependencies
*   **`data-mqtt-api`**: Implements the contracts from this module.
*   **`kmqtt`**: Multiplatform MQTT client library.
*   **`kotlinx-serialization`**: For JSON payload formatting.
