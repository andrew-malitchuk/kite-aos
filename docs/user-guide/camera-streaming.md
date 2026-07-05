# Camera Streaming

Kite can stream the tablet's front camera as an MJPEG feed over your local network. The same camera used for [motion detection](motion-detection.md) doubles as a live video source — no separate hardware needed.

When [MQTT is active](home-assistant-and-mqtt.md), the stream URL is automatically published to Home Assistant via MQTT Discovery. It appears as a sensor entity, and you can reference it directly in a camera card — no credentials, no manual YAML required.

---

## How It Works

```
Tablet front camera
        │
        ▼ CameraX (640×480)
  MJPEG HTTP server
        │
        ├─── GET /stream.mjpg   — continuous multipart stream
        └─── GET /snapshot.jpg  — single JPEG frame

        │ (when MQTT enabled)
        ▼
  MQTT Discovery → sensor.<clientId>_camera_url
```

The HTTP server runs locally on the tablet. It is open — there is no authentication — so it should only be accessible on your trusted local network.

---

## Enabling Camera Streaming

1. Open **Settings → Camera streaming**.
2. Flip the master toggle to **on**.
3. Kite starts the HTTP server immediately. The stream is accessible at:

```
http://<tablet-ip>:<port>/stream.mjpg
http://<tablet-ip>:<port>/snapshot.jpg
```

If you don't know the tablet's IP address, check **Settings → Wi-Fi** on the device, or read it from the `sensor.<clientId>_camera_url` entity in Home Assistant once MQTT is connected.

---

## Settings Reference

Navigate to **Settings → Camera streaming**.

| Setting | Range | Default | What it does |
|---|---|---|---|
| **Enabled** | toggle | off | Starts or stops the MJPEG HTTP server. |
| **Port** | 1–65535 | `8080` | TCP port the HTTP server listens on. Change if another service occupies `8080`. |
| **Quality** | 1–100 | `75` | JPEG compression quality. Higher values produce sharper frames but increase bandwidth. |
| **FPS** | 1–30 | `10` | Target frame rate. The actual rate may be lower on slower devices. |
| **Rotation** | 0 / 90 / 180 / 270 | `0°` | Rotates the output frame to match your tablet's physical mounting orientation. |

!!! info "Camera resolution"
    When streaming is enabled, the camera binds at **640×480**. When disabled (motion detection only), it drops to **176×144** to save resources.

---

## Home Assistant Integration

### Auto-Discovery via MQTT

When MQTT is enabled, Kite automatically registers a sensor in Home Assistant:

| Entity | Value |
|---|---|
| `sensor.<clientId>_camera_url` | `http://<tablet-ip>:<port>/stream.mjpg` when streaming is on, empty when off. |

The entity updates immediately whenever streaming is toggled or the port changes. No HA restart or YAML editing required.

To verify: open **Home Assistant → Settings → Devices & Services → MQTT** and find your Kite device. The `Camera Stream URL` sensor should be listed there.

### Option A — MJPEG IP Camera (UI, no YAML)

Home Assistant has a built-in **MJPEG IP Camera** integration that can be added entirely through the UI — no `configuration.yaml` changes required, and no credentials needed.

1. Go to **Settings → Devices & Services → Add Integration**.
2. Search for **MJPEG IP Camera** and select it.
3. Fill in the dialog:

| Field | Value |
|---|---|
| **Name** | `Kite — Hallway Tablet` (or any label you prefer) |
| **MJPEG URL** | `http://192.168.1.x:8080/stream.mjpg` |
| **Still image URL** | `http://192.168.1.x:8080/snapshot.jpg` (optional) |
| **Username** | *(leave empty)* |
| **Password** | *(leave empty)* |
| **Verify SSL** | *(uncheck — HTTP only)* |

4. Click **Submit**. Home Assistant creates a `camera.*` entity immediately — no restart needed.

The IP and port come from the `sensor.<clientId>_camera_url` entity value. If the tablet's IP changes (e.g., after a DHCP renewal), update the integration entry via **Settings → Devices & Services → MJPEG IP Camera → Configure**.

### Option B — Generic Camera (YAML)

If you prefer to manage integrations via `configuration.yaml`, add:

```yaml
camera:
  - platform: generic
    name: "Kite — Hallway Tablet"
    still_image_url: "http://192.168.1.x:8080/snapshot.jpg"
    stream_source: "http://192.168.1.x:8080/stream.mjpg"
```

Replace `192.168.1.x` and `8080` with the values from the `sensor.<clientId>_camera_url` entity.

After adding the entry, restart Home Assistant. The camera entity appears in **Settings → Entities** and can be added to any dashboard.

!!! tip "Use a template sensor"
    Instead of hardcoding the IP, you can reference the MQTT sensor directly in a template so the camera URL stays in sync automatically:
    ```yaml
    camera:
      - platform: generic
        name: "Kite — Hallway Tablet"
        still_image_url: >-
          {{ states('sensor.kite_hallway_tablet_camera_url') | replace('stream.mjpg', 'snapshot.jpg') }}
        stream_source: "{{ states('sensor.kite_hallway_tablet_camera_url') }}"
    ```

### Dashboard Card

After the camera entity is created, add a **Picture Glance** or **Picture Entity** card on any Lovelace dashboard:

```yaml
type: picture-entity
entity: camera.kite_hallway_tablet
show_state: false
show_name: false
```

---

## Troubleshooting

**Stream URL is empty in Home Assistant** — streaming is disabled in Kite. Go to **Settings → Camera streaming** and enable the toggle.

**Cannot reach the stream from a browser** — verify the tablet and the browser are on the same subnet. Check that no firewall on the tablet or router is blocking the configured port.

**Black frame or no image** — the camera may be in use exclusively by another app. Restart the Kite app or reboot the tablet.

**High latency / choppy video** — lower **FPS** to 5–8 and reduce **Quality** to 50–60. MJPEG is uncompressed between frames, so high FPS at high quality saturates Wi-Fi quickly.

**Wrong orientation** — adjust **Rotation** in **Settings → Camera streaming** to match the physical mounting angle of the tablet.
