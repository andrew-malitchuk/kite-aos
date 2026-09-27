# data-mqtt-api

MQTT communication contracts for the "kite-aos" project.

## Features
- **Telemetry Contract**: Defines methods for reporting device state via MQTT.
- **Connection Management**: Interface for handling broker credentials and lifecycle.
- **Form-Factor Reporting** (`@since 1.2.0`): `connect(...)` accepts a `model` parameter (`"tv"`/`"tablet"`) that is surfaced to Home Assistant and used to gate form-factor-specific entities.
- **Diagnostic Opt-Outs** (`@since 2.1.0`): `connect(...)` takes a `diagnostics: Set<String>` of entity ids (`uptime`, `app_version`, `ip_address`, `ram_usage`). Ids absent from the set are actively unregistered rather than merely left unpublished, so turning one off removes it from Home Assistant instead of stranding it at its last value.
- **Companion Telemetry** (`@since 2.1.0`): `sendCompanionTelemetry(...)` publishes uptime, app version, LAN IP and RAM usage as plain scalars, skipping any value whose entity was not requested at connect time.
- **Dashboard Reachability** (`@since 2.2.0`): `sendDashboardState(isReachable)` reports whether the dashboard backend is answering. Kept separate from device availability — the panel itself is healthy during a backend outage, so marking the whole device unavailable would hide its working controls.
- **Discovery Teardown** (`@since 2.1.0`): `purgeDiscovery()` publishes empty retained payloads to every `config` topic, which is how MQTT Discovery expresses deletion. `disconnect()` additionally publishes a retained `offline` availability payload, because a graceful DISCONNECT makes the broker discard the Last Will.
- **Remote Command Channel** (`@since 2.2.0`): alongside the per-entity command topics, `observeCommands()` also emits the shared `{clientId}/command/set` topic, which carries a JSON envelope `{ "action": "...", "value": "..." }` rather than a bare value.

## Usage
Interact with this module via the `TelemetryMqttSource` interface to send updates to an MQTT broker.
