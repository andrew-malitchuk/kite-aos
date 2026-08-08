# data-mqtt-impl

Implementation of the MQTT API for the "kite-aos" project.

## Features
- **Robust Client**: Built on `kmqtt` with automatic background reconnection.
- **HA MQTT Discovery**: Automatic integration with Home Assistant for effortless device monitoring.
- **Telemetry**: Reports real-time motion detection and battery levels.
- **Form-Factor Entity Gating** (`@since 1.2.0`): The reported `model` (`"tv"`/`"tablet"`) is included in discovery payloads. On Android TV the battery, volume, brightness and screen entities are not registered (they cannot be controlled there), their command topics are not subscribed, and any stale registrations from a prior tablet setup are cleared.

## Configuration
The implementation is provided as a singleton via Koin. It manages its own internal coroutine scope for connection handling.
