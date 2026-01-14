# data-mqtt-impl

Implementation of the MQTT API for the "kite-aos" project.

## Features
- **Robust Client**: Built on `kmqtt` with automatic background reconnection.
- **HA MQTT Discovery**: Automatic integration with Home Assistant for effortless device monitoring.
- **Telemetry**: Reports real-time motion detection and battery levels.

## Configuration
The implementation is provided as a singleton via Koin. It manages its own internal coroutine scope for connection handling.
