# data-mqtt-api

MQTT communication contracts for the "kite-aos" project.

## Features
- **Telemetry Contract**: Defines methods for reporting device state via MQTT.
- **Connection Management**: Interface for handling broker credentials and lifecycle.
- **Form-Factor Reporting** (`@since 1.2.0`): `connect(...)` accepts a `model` parameter (`"tv"`/`"tablet"`) that is surfaced to Home Assistant and used to gate form-factor-specific entities.

## Usage
Interact with this module via the `TelemetryMqttSource` interface to send updates to an MQTT broker.
