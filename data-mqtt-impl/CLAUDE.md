# Module: data-mqtt-impl

## Overview
This module provides a concrete implementation of the MQTT API using the **kmqtt** library. It includes advanced features like automatic reconnection and Home Assistant MQTT Discovery.

## Responsibilities
*   **MQTT Client Management**: Implements connection logic using `kmqtt`, including a background reconnection loop and state management.
*   **Home Assistant Integration**: Automatically registers the device, its sensors and its controls in Home Assistant using MQTT Discovery topics, and unregisters the ones that do not apply.
*   **Availability**: Registers an MQTT Last Will so the broker marks the panel unavailable when it drops off, and publishes `online`/`offline` on the device availability topic.
*   **Telemetry Publishing**: Handles the actual transmission of telemetry data to the broker with appropriate topics and payloads.
*   **Dependency Injection**: Configures Koin modules for providing the `TelemetryMqttSource` implementation.

## Architecture
This module implements `TelemetryMqttSource` using a background `Job` for connection management and `kotlinx-serialization` for discovery payloads.

### Key Components
*   **`data.mqtt.impl.source.datasource.TelemetryMqttSourceImpl`**: The core implementation managing the `MQTTClient` lifecycle and publishing logic.
*   **`data.mqtt.impl.source.resources.*`**: Internal DTOs used for Home Assistant discovery configuration, one per entity.
*   **`data.mqtt.impl.di.DataMqttImplModule`**: Koin module for providing the MQTT source.

### Form-Factor Entity Gating (`@since 1.2.0`, Android TV)
`connect(...)` now receives a `model` parameter (`"tv"`/`"tablet"`) that is emitted in every discovery `device` block via the new `DeviceMqtt.model` field, so Home Assistant shows the device's form-factor.

When `model == "tv"`, the implementation adapts because several controls are not meaningful or reliably controllable on Android TV (no battery; audio owned by the TV/receiver over HDMI/ARC; backlight not exposed via `Settings.System`; screen locking needs a Device Administrator that Android TV lacks):
*   **Skipped registrations**: the battery, volume, brightness and screen entities are not registered on TV. Motion, URL, FAB, screensaver, camera-URL and clear-cache entities are always registered on both form-factors, as are the opted-in diagnostic sensors — none of them depend on hardware the TV lacks.
*   **Skipped command subscriptions**: `subscribeToCommandTopics(...)` omits the `volume/set`, `brightness/set` and `screen/set` topics on TV.
*   **Stale-registration cleanup**: `unregisterEntity(...)` publishes an empty retained payload to the config topics for the brightness, screen, volume and battery entities, clearing any registration left over from a previous mobile (tablet) form-factor so those entities disappear from Home Assistant.

### Entity Set (`@since 2.1.0`)

Every discovery config carries `availability_topic`, and the diagnostic / config entities carry an
`entity_category` so they sit off the device's primary control card instead of crowding out the real
controls.

| Domain | Entity | Category | Notes |
|:--|:--|:--|:--|
| `binary_sensor` | `motion` | — | Primary presence signal |
| `binary_sensor` | `dashboard` | `diagnostic` | Backend reachability (`connectivity`); separate from device availability on purpose |
| `sensor` | `battery` | `diagnostic` | Mobile only |
| `sensor` | `url` | `diagnostic` | Current dashboard URL |
| `sensor` | `camera_url` | `diagnostic` | MJPEG stream URL |
| `sensor` | `uptime` | `diagnostic` | Opt-out; `total_increasing`, so a reboot reads as a counter reset |
| `sensor` | `app_version` | `diagnostic` | Opt-out |
| `sensor` | `ip_address` | `diagnostic` | Opt-out |
| `sensor` | `ram_usage` | `diagnostic` | Opt-out; `measurement`, device-wide |
| `number` | `volume` / `brightness` | — | Mobile only |
| `switch` | `screen` | — | Mobile only |
| `switch` | `fab` | `config` | Optimistic |
| `switch` | `screensaver` | — | Optimistic |
| `button` | `clear_cache` | `config` | Flushes caches only, preserving the HA session |
| `button` | `reload` | `config` | Publishes onto the shared command topic |
| `button` | `navigate_home` | `config` | Publishes onto the shared command topic |
| `text` | `navigate` | `config` | Optimistic URL field; publishes onto the shared command topic |

**No entity uses a Jinja `value_template`.** Booleans travel as literal `payload_on`/`payload_off`
strings, which sidesteps the standard MQTT-discovery trap where Jinja renders `True`/`False`
capitalised while `payload_on` is conventionally lowercase — a mismatch that makes the state
comparison silently never match. Any future templated `binary_sensor` must end its template in
`| lower`.

### Remote Command Channel (`@since 2.2.0`)

The single-purpose command topics (`{clientId}_fab/fab/set`, `{clientId}_screensaver/screensaver/set`, …)
each drive exactly one entity. WebView control is not shaped like that: `navigate`, `reload`,
`back`, `forward`, `clear_cache`, `navigate_home` and `evaluate_js` are one vocabulary aimed at one
target, so they share one topic.

*   **Topic**: `{clientId}/command/set` — deliberately directly under the client prefix rather than
    beside an entity, because it belongs to no single entity.
*   **Payload**: `{"action": "<name>", "value": "<optional>"}`. `value` is required by `navigate`
    (the URL) and `evaluate_js` (the snippet), and ignored by the rest.
*   **Decoding** happens in `ObserveMqttRemoteCommandUseCaseImpl` via `RemoteCommandModel.of(...)`.
    Malformed JSON, unknown actions and missing values are dropped silently — these payloads are
    typed by hand into Home Assistant automations, so a bad one is expected traffic, and killing
    the collector over it would take the whole command channel down with it.

Only three actions get a discovery entity; the rest are reachable on the same topic but would only
crowd the device card if every one of them became a button nobody presses by hand:

| Domain | Entity | Publishes |
|:--|:--|:--|
| `button` | `reload` | `{"action": "reload"}` |
| `button` | `navigate_home` | `{"action": "navigate_home"}` |
| `text` | `navigate` | `command_template` wraps the typed URL into the envelope |

The `text` entity declares **no `state_topic`** and is therefore optimistic. Mirroring the panel's
real URL there would break the entity outright the moment a dashboard URL passes Home Assistant's
255-character text limit — and that URL is already published as its own `url` sensor.

`clear_cache` is reachable both ways: through its own dedicated button (its own topic, `@since 2.1.0`)
and as an action on this channel. Both land on the same `RemoteCommandBus.emitClearCache()`.

### Availability & Last Will (`@since 2.1.0`)
The device availability topic is `{clientId}/availability`, carrying `online` / `offline`; every
entity references it, so one topic governs the whole device — the app is either connected or it is
not.

*   **Ungraceful loss** (crash, kill, power cut) is covered by the MQTT Last Will, registered on the
    `MQTTClient` with `willRetain = true`. The broker publishes it without anything needing to
    notice the panel is gone.
*   **Graceful shutdown** publishes `offline` explicitly *before* DISCONNECT, because a clean
    DISCONNECT makes the broker discard the will — otherwise a deliberate shutdown would leave the
    panel looking healthy.
*   **`keepAlive` stays at the library default (60 s).** It cannot simply be lowered to detect
    absence sooner: `kmqtt` only sends PINGREQ inside the window between 0.9x and 1.0x `keepAlive`,
    and the connection loop polls `step()` every 5 s. A shorter `keepAlive` narrows that window
    below the poll interval, so the client skips its own ping and trips its keep-alive timeout.

### Discovery Teardown (`@since 2.1.0`)
Discovery configs are **retained** messages, so an entity persists in Home Assistant until an empty
payload replaces it. Two paths clear them:

*   **`purgeDiscovery()`** walks `ALL_ENTITIES` — every entity the client can ever register,
    regardless of current form factor or opt-outs — and is called when the user disables MQTT,
    *before* disconnecting. Without it, disabling MQTT strands every entity in Home Assistant with
    nothing left to update it, leaving the user to delete them by hand.
*   **Per-connection reconciliation** in `registerEntities(...)` unregisters whatever does not apply
    now: form-factor-inappropriate entities, and diagnostic sensors the user has opted out of.

### Diagnostic Opt-Outs (`@since 2.1.0`)
`connect(...)` takes a `diagnostics: Set<String>` of entity ids (`"uptime"`, `"app_version"`,
`"ip_address"`, `"ram_usage"`). Ids in the set are registered and published; ids absent are
unregistered and skipped on publish. The domain enum `MqttDiagnosticEntityModel` is translated to
these plain string ids in `MqttRepositoryImpl` — the data modules do not depend on `domain-core`, so
the repository is the seam.

## Dependencies
*   **`data-mqtt-api`**: Implements the contracts from this module.
*   **`kmqtt`**: Multiplatform MQTT client library.
*   **`kotlinx-serialization`**: For JSON payload formatting.
