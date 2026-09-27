# Home Assistant & MQTT

Kite integrates with Home Assistant in two ways: it loads your HA dashboard as the kiosk URL, and it can report device state back to HA via MQTT. This page covers both.

---

## Dashboard URL

The Dashboard URL is the page Kite loads on startup. For Home Assistant this is typically:

```
http://<your-ha-ip>:8123
```

or a specific dashboard path:

```
http://192.168.1.100:8123/lovelace/kiosk
```

You can set this in **Settings → Web kiosk → Dashboard URL**, or during the [onboarding wizard](first-time-setup.md#step-3--url-configuration).

### Scan for Home Assistant

If you don't know your HA IP address, use the built-in discovery:

1. Go to **Settings → Web kiosk → Scan for Home Assistant**.
2. Kite scans your local network using mDNS and a network sweep.
3. A dialog lists all discovered instances, labeled *mDNS* or *Network scan*.
4. Tap an entry to fill the Dashboard URL automatically.

!!! tip
    mDNS results appear faster. If your router blocks mDNS, the network scan will still find HA as long as both devices are on the same subnet.

---

## MQTT Integration

With MQTT configured, Kite publishes device telemetry (battery level, motion events) and auto-registers itself in Home Assistant via the discovery protocol — no manual entity configuration needed.

### Requirements

Before enabling MQTT, make sure:

- A valid Dashboard URL is already set (the MQTT toggle is gated on this).
- All six MQTT fields below are filled in.

### Settings Reference

Navigate to **Settings → MQTT**.

| Setting | What to enter |
|---|---|
| **IP Address** | Hostname or IPv4 address of your MQTT broker (e.g. `192.168.1.50` or `homeassistant.local`). |
| **Port** | Broker port. The standard unencrypted port is `1883`. |
| **Client ID** | A unique identifier for this device on the broker (e.g. `kite-hallway-tablet`). |
| **Username** | Broker login username. |
| **Password** | Broker login password (masked). |
| **Friendly Name** | Display name reported in HA discovery payloads (e.g. `Hallway Kiosk`). This is the name that will appear in your Home Assistant entities list. |

After filling all fields, flip the **MQTT** master toggle to enable the connection.

!!! info "Debounce"
    Text fields save after roughly 1 second of no typing. Toggle changes save immediately.

### Home Assistant Auto-Discovery

When MQTT is active, Kite publishes standard Home Assistant discovery payloads. Your device appears automatically under **Settings → Devices & Services → MQTT** in HA with sensors for:

- Battery level and charging state
- Motion detected (binary sensor)
- Current dashboard URL and dashboard reachability (see [Diagnostic Sensors](#diagnostic-sensors) below)
- Camera stream URL (when [Camera Streaming](camera-streaming.md) is enabled)

No YAML configuration required on the HA side.

#### Diagnostic Sensors

Kite also publishes device-health sensors, grouped under the device's *Diagnostic* category in HA so they don't crowd the main entity card:

| Entity | Reports |
|---|---|
| `binary_sensor.<clientId>_dashboard` | Whether the panel can currently reach the dashboard backend. Deliberately separate from the device's own MQTT availability — during a Home Assistant restart the panel itself is healthy, so it stays "on" here even while HA is down. |
| `sensor.<clientId>_uptime` | Seconds since the app last (re)started. |
| `sensor.<clientId>_app_version` | The installed Kite version. |
| `sensor.<clientId>_ip_address` | The tablet's current WiFi IP. |
| `sensor.<clientId>_ram_usage` | Current RAM usage percentage. |

Each of the four diagnostic sensors can be turned off individually in **Settings → MQTT** if you don't want it cluttering your entity list.

#### Remote Commands

A `text.<clientId>_navigate` entity and two buttons — `button.<clientId>_reload` and `button.<clientId>_navigate_home` — let you drive the kiosk from Home Assistant automations without touching the tablet. There's also a `button.<clientId>_clear_cache` for the one-click fix when a dashboard is stuck showing stale assets: it flushes the WebView's HTTP/image caches and reloads, without signing you out of Home Assistant.

For scripting beyond what those entities expose (back/forward navigation, or evaluating a snippet of JavaScript in the page), publish directly to the command topic:

```
{clientId}/command/set
```

with a JSON payload:

```json
{ "action": "navigate", "value": "http://192.168.1.100:8123/lovelace/kiosk" }
```

Supported `action` values: `navigate` (needs `value`, the URL to load), `reload`, `back`, `forward`, `clear_cache`, `navigate_home`, and `evaluate_js` (needs `value`, the JS to run — Android WebView engine only; ignored on GeckoView).

#### Camera Stream URL Sensor

When camera streaming is enabled, Kite publishes the live MJPEG endpoint as a sensor:

| Entity | Example value |
|---|---|
| `sensor.<clientId>_camera_url` | `http://192.168.1.x:8080/stream.mjpg` |

The sensor value is empty when streaming is off, and updates automatically when the port changes. You can use this URL directly in a `camera: generic` entry in `configuration.yaml` — no username or password needed. See [Camera Streaming → Home Assistant Integration](camera-streaming.md#home-assistant-integration) for the full setup.

---

## Troubleshooting

**MQTT toggle is greyed out** — check that the Dashboard URL is set and all six MQTT fields are non-empty.

**Device does not appear in Home Assistant** — verify the broker IP/port are reachable from the tablet (try pinging from another device), and confirm the username and password are correct.

**mDNS scan returns no results** — your router may be blocking mDNS. Try the Network scan option, or enter the IP address manually.
