# data-mqtt-impl

Implementation of the MQTT API for the "kite-aos" project.

## Features
- **Robust Client**: Built on `kmqtt` with automatic background reconnection.
- **HA MQTT Discovery**: Automatic integration with Home Assistant for effortless device monitoring.
- **Telemetry**: Reports real-time motion detection and battery levels, plus periodic companion diagnostics (uptime, app version, IP address, device RAM usage).
- **Availability** (`@since 2.1.0`): An MQTT Last Will on `{clientId}/availability` marks the panel unavailable in Home Assistant when it drops off; a graceful shutdown publishes `offline` explicitly, since a clean DISCONNECT makes the broker discard the will.
- **Entity Categories** (`@since 2.1.0`): Diagnostic sensors carry `entity_category: diagnostic` and the FAB / clear-cache entities `config`, keeping the device's primary control card free of clutter.
- **Discovery Teardown** (`@since 2.1.0`): Disabling MQTT purges every registered entity by publishing empty retained payloads to their config topics, instead of stranding them in Home Assistant.
- **Remote Cache Flush** (`@since 2.1.0`): A `clear_cache` button drops the WebView's HTTP and image caches and reloads — cookies and auth sessions are preserved, so the panel stays signed in to Home Assistant.
- **Remote Command Channel** (`@since 2.2.0`): One topic — `{clientId}/command/set`, payload `{"action": "…", "value": "…"}` — carries the whole WebView vocabulary (`navigate`, `reload`, `back`, `forward`, `clear_cache`, `navigate_home`, `evaluate_js`), so an automation learns one topic instead of one per action. `reload`, `navigate_home` and a `navigate` text field are exposed as discovery entities; the rest stay scripting-only. Malformed payloads are dropped, never fatal.
- **Per-Entity Opt-Outs** (`@since 2.1.0`): Each diagnostic sensor can be switched off individually in Settings; switching one off unregisters it rather than merely muting it.
- **Form-Factor Entity Gating** (`@since 1.2.0`): The reported `model` (`"tv"`/`"tablet"`) is included in discovery payloads. On Android TV the battery, volume, brightness and screen entities are not registered (they cannot be controlled there), their command topics are not subscribed, and any stale registrations from a prior tablet setup are cleared.

## Configuration
The implementation is provided as a singleton via Koin. It manages its own internal coroutine scope for connection handling.
