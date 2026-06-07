# Roadmap

Community-driven feature backlog. Items sourced from GitHub issues and Reddit feedback.

---

## In Progress / Next

### Screensaver
Idle screensaver that activates after a configurable timeout, overlaid on the dimmed WebView.

- Fullscreen slideshow from a local folder or Unsplash (configurable source)
- Clock overlay (time + date)
- Entity-status display: show live Home Assistant sensor values (temperature, humidity, etc.) via JS bridge from the already-loaded WebView session — no separate HA client needed
- Configurable activation delay and transition interval
- Dismisses on motion detection (integrates with existing CameraX pipeline)
- MQTT command to force-activate / dismiss
- `MotionService` emits `ACTIVE | SCREENSAVER | LOCKED` screen-state via a new use case; `MainScreen` renders the overlay in reaction to that state

---

## Planned

### Auto Reboot
Schedule automatic device reboots (e.g. 2 AM every two weeks) for long-running kiosk deployments.

- `AlarmManager` + `DevicePolicyManager.reboot()` (requires device owner)
- Configurable time and recurrence interval in Settings

### MQTT TLS + DNS
Improve MQTT connectivity for production deployments.

- Accept hostnames in addition to IP addresses for broker address
- TLS support with configurable certificate (port 8883)

### Front Light Color Temperature
Adjust the warmth/coolness of the display front light where hardware supports it.

- Investigate `NIGHT_DISPLAY_*` / display color matrix APIs
- Device-specific availability — expose only when supported

### Light Sensor Dimming
Use the device's ambient light sensor as an alternative (or complement) to camera-based motion detection for screen brightness control.

- Read `TYPE_LIGHT` sensor values
- Map lux levels to configurable brightness thresholds
- Can run independently of or alongside the existing CameraX motion detector
- Useful for devices without a usable front camera

---

## Done

### Camera Streaming (MJPEG) ✓
Expose the tablet's front camera as an MJPEG stream consumable by Home Assistant or any browser.

- Embedded HTTP server (`MjpegHttpServer`) running inside `MotionService` foreground service
- Two endpoints: `/stream.mjpg` (continuous multipart stream) and `/snapshot.jpg` (single frame)
- Stream accessible on the local network (e.g. `http://192.168.1.x:8080/stream.mjpg`)
- Configurable port, quality (0–100), and FPS in Settings
- Toggle via Settings; camera rebinds to 640×480 when enabled, 176×144 when disabled
- No data leaves the local network

### Volume Control via MQTT ✓
Receive and publish device volume over MQTT.

- Topic: `{clientId}_volume/volume/set` (inbound, 0–100)
- Publishes confirmed volume state back to broker on change
- Integrated with HA discovery

### Screen Brightness via MQTT ✓
Control screen brightness remotely from Home Assistant or any MQTT client.

- Topic: `{clientId}_brightness/brightness/set` (inbound, 0–255)
- Publishes confirmed brightness state back to broker on change
- Requires `WRITE_SETTINGS` permission (already declared)
- Integrated with HA discovery via `BrightnessConfigMqtt`
- Mirrors the volume MQTT implementation

### Home Assistant Discovery ✓
Detect Home Assistant instances on the local network from the onboarding wizard.

- mDNS hostname probes (`homeassistant.local`, etc.) with 900 ms timeout
- Parallel subnet scan fallback
- `DiscoverHomeAssistantUseCase` + `HomeAssistantScannerImpl`

### Backup / Restore Configuration ✓
Export all app settings to a single JSON file and restore them on any device.

- `onExportConfig()` in `SettingsViewModel` reads current state and serializes to JSON (`kotlinx.serialization`)
- `onImportConfigContent()` parses the JSON and writes each preference back via existing use cases; onboarding state excluded
- Export/Import UI in Settings — Android share sheet for export, SAF file picker for import
- Config JSON includes a `version` field for forward-compatibility
- Enables quick migration between tablets and multi-device deployments

### Settings: Subtitle Hints ✓
Added descriptive subtitle/hint text below hard-to-understand settings to reduce support questions.

- `BaseListItem`, `ToggleListItem`, `NumberInputListItem`, `SimpleListItem`, `SectionToggleItem` all support optional `subtitle` parameter
- Hints added for: motion detector, sensitivity, dim delay, screen timeout, FAB delay, MQTT section, auto-return, WebView refresh

### FAB Control via MQTT ✓
Show or hide the floating action button remotely from Home Assistant.

- Subscribe to `{clientId}_fab/fab/set` inbound MQTT topic
- `ObserveMqttFabCommandUseCase` filters the shared command flow, maps `"ON"` / `"OFF"` → `Boolean`
- `MainViewModel.observeFabCommand()`: `"ON"` triggers `onMotionDetected()` (shows FAB + timer), `"OFF"` hides immediately
- HA Discovery: `switch` entity with `optimistic: true`, icon `mdi:gesture-tap-button`

### Low Motion / Reduce Animations ✓
Disables all Android animations globally for a smoother experience on slow kiosk hardware.

- `ReduceMotionPreference` in Proto DataStore → full pipeline (serializer, source, repository, use cases)
- Toggle in Settings → UX section, with subtitle hint
- `HostActivity` applies `ValueAnimator.setDurationScale(0f)` via reflection (works API 26+) whenever the preference changes

### Periodic WebView Refresh ✓
Automatically reload the dashboard at a configurable interval.

- Toggle + interval (seconds) in Settings under a dedicated WebView Refresh section
- Coroutine timer in `MainViewModel` — cancels/restarts on settings change
- Auto-disables when interval is set to 0

### UI Polish: Custom Controls & Loading State ✓
Replaced Material3 defaults with design system components; added shimmer loading experience.

- All toggle controls use the custom `Toggle` component via `ToggleListItem` / `SectionToggleItem` — no Material3 Switch remains
- `ShimmerImage` overlay displayed over the WebView while the page loads; debounced 400 ms to suppress auth-redirect flicker

---

## Content

### Demo Video
A short screen-recorded walkthrough of the main features:
onboarding → dashboard → motion wake → control drawer → settings → MQTT in HA.
