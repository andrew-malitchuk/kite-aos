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

### Companion HA Entities ✓
Expand Home Assistant Discovery so the tablet appears as a full companion device — not just a brightness/volume MQTT client.

- New diagnostic sensors: `uptime` (`SystemClock.elapsedRealtime()`, `total_increasing` so a reboot reads as a counter reset), `app_version`, `ip_address`, `ram_usage` — all `entity_category: diagnostic`, sampled once a minute by `DeviceTelemetryProvider` and published via `MqttSendCompanionTelemetryUseCase`
- Each of the four sensors is individually opt-outable in Settings → MQTT; the dashboard URL was already published as the `url` sensor, so no separate `current_url` entity was needed
- The originally-planned `last_seen` binary sensor was superseded by a purpose-built `dashboard` reachability binary sensor instead — see Dashboard Connection Monitor below
- Diagnostic opt-outs thread end-to-end via a `diagnostics: Set<String>` param on `TelemetryMqttSource.connect(...)`, translated from the domain `MqttDiagnosticEntityModel` enum in `MqttRepositoryImpl`

### Inactivity Page Reset ✓
Automatically navigate back to the configured home URL after the user stops interacting with the dashboard for a configurable period.

- Idle timeout (minutes) stored in Proto DataStore; configurable in Settings → WebView section
- Touch events forwarded from `GeckoView` / `WebView` reset a coroutine-based countdown timer in `MainViewModel`
- On expiry: `MainViewModel` dispatches a `NavigateHome` intent that calls `loadUrl(homeUrl)` — reuses the existing home-URL preference already in DataStore
- Timer is paused while the screen is off or the screensaver is active (integrates with `MotionService` screen-state flow)
- Setting of `0` disables the feature entirely
- MQTT command `inactivity_reset/trigger` forces an immediate reset remotely

### Volume Button Gesture for Settings ✓
Open the control drawer by pressing a physical volume button N times in quick succession — no visible UI required during kiosk operation.

- `HostActivity.dispatchKeyEvent` intercepts `KEYCODE_VOLUME_UP` / `KEYCODE_VOLUME_DOWN`; counts presses within a 2-second rolling window
- Configurable press count (default 5) stored in DataStore; configurable in Settings → Access section
- On threshold reached: emits the same side effect that the FAB press fires — opens the control drawer via `AppNavigator`
- Volume key events consumed only for the gesture; when count is not reached the key event propagates normally to the system
- Can be disabled via Settings toggle for deployments that need hardware volume control intact

### Time-Based Sleep / Wake Scheduler
Define daily on/off rules so the kiosk screen powers down overnight and wakes at a configured time — no Home Assistant automation required.

- Data model: list of `ScheduleRule(id, type: WAKE|SLEEP, hour, minute, enabled)` serialized via `kotlinx.serialization` and stored in DataStore
- `AlarmManager.setAlarmClock()` used for each active rule; alarms re-registered on `ACTION_BOOT_COMPLETED`, `ACTION_TIME_CHANGED`, and `ACTION_TIMEZONE_CHANGED`
- On SLEEP alarm: calls existing `DevicePowerManager.dimScreen()` → `goToSleep()` pipeline
- On WAKE alarm: calls `DevicePowerManager.wakeScreen()` + re-arms the next occurrence 24 h later
- New sub-screen in Settings → Display: list of rules with add/edit/delete, toggle per rule, time picker
- Requires `SCHEDULE_EXACT_ALARM` permission (already targeted API 31+); prompts user if not granted
- Overnight schedules (e.g. sleep 23:00 → wake 07:00) handled by sorting rules chronologically and scheduling the next future alarm

### Remote MQTT Commands ✓
Accept inbound MQTT commands to control WebView navigation and state remotely from Home Assistant or any MQTT client.

- Subscription topic: `{clientId}/command/set`; payload is a JSON object `{ "action": "...", "value": "..." }`
- Supported actions: `navigate` (load arbitrary URL), `reload` (refresh current page), `back` (WebView history back), `forward`, `clear_cache`, `navigate_home` (load configured home URL), `evaluate_js` (execute a JS snippet in the page)
- `ObserveMqttRemoteCommandUseCase` filters the shared MQTT command flow and maps payloads to the `RemoteCommandModel` sealed class in `domain-core`; malformed JSON, unknown actions and missing values are dropped rather than tearing down the collector
- `MqttService.observeRemoteCommands()` translates each command onto `RemoteCommandBus`; `MainViewModel` turns bus emissions into side effects, and `MainContent` applies them to the mounted engine through a single `EngineCommand` channel
- `evaluate_js` needs an evaluation entry point the engine must expose — honoured on Android WebView, ignored with a log on GeckoView, which blocks `javascript:` loads
- HA Discovery: `button` entity for `reload` and `navigate_home`; optimistic `text` entity for `navigate` URL input. The remaining actions stay reachable on the topic without an entity, so the device card is not buried under buttons nobody presses by hand
- All commands logged at DEBUG level

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

### Acoustic Detection
Use the device's microphone to detect ambient sound as a presence signal — an alternative or complement to camera-based motion detection, useful for devices without a usable front camera (e.g. Android TV boxes).

- Read raw audio amplitude via `AudioRecord` or `MediaRecorder.getMaxAmplitude()` at a configurable polling interval
- Configurable sensitivity threshold (dB level) — above threshold fires the same `MOTION_DETECTED` event as CameraX
- Runs inside `MotionService` as an optional detection mode; can operate alongside or independently of CameraX
- Toggle in Settings → Motion section; disabled by default (requires microphone permission)
- Publishes presence state via MQTT identically to camera-based detection
- Particularly useful for Android TV deployments where USB webcam is absent but a USB mic or built-in mic is available

---

## Done

### Wake & WiFi Lock ✓
Keep the MQTT session and dashboard alive through the screen-off window instead of losing the WiFi radio to OEM power management or Doze.

- `LockManager` (`presentation-core-platform/source/power/`) holds a `PARTIAL_WAKE_LOCK` unconditionally for the life of the process, plus a `WifiLock` (`WIFI_MODE_FULL_LOW_LATENCY` on API 29+, `WIFI_MODE_FULL_HIGH_PERF` below)
- WiFi lock is a user-disableable toggle in Settings → Resilience, defaulted on — some budget WiFi chipsets wedge the driver if the radio is held out of power-save for hours
- Started once from `Application.onCreate` rather than tied to the motion/MQTT foreground services, so a plain-dashboard deployment running neither still gets the locks
- No explicit teardown — both locks live for the process lifetime; the framework reclaims them on death

### Crash Auto-Relaunch ✓
Bring the kiosk back automatically after a fatal crash instead of leaving an unattended device sitting on the launcher.

- Custom `UncaughtExceptionHandler` (`presentation-core-platform/source/diagnostics/`) persists a crash report and schedules a one-shot `AlarmManager` alarm to relaunch `HostActivity`, then lets the process die — chains to the previous handler so Crashlytics still sees the crash on the `gms` flavor
- Rate-limited: three crashes within a rolling 10-minute window suppress the relaunch to avoid a battery-draining boot loop
- Toggle in Settings → Resilience, default on; the flag is mirrored into `SharedPreferences` rather than DataStore since a crashing process cannot reliably await a suspend call
- Crash reports land in app-private storage via `CrashDiagnosticsStore`; a Settings → Diagnostics viewer is still to come

### Memory Recovery & Scheduled Reload ✓
Reset the WebView's accumulated GPU/JS memory before it turns into a renderer kill or OOM on a dashboard that runs for weeks.

- Stealth reload fires only while the screensaver is active, invisible to the user; a separate daily scheduled reload fires at a configured hour regardless of state
- Both scheduled via `AlarmManager.setAndAllowWhileIdle()` rather than an in-process timer, since Doze freezes coroutine delays during exactly the overnight window the reload exists for
- `onTrimMemory`/low-memory callbacks trigger the same reload path, deferred until the screensaver engages if the screen is currently on
- Configurable in Settings → Resilience: memory-recovery toggle, scheduled-reload toggle, and reload hour (0–23)

### Dashboard Connection Monitor ✓
Detect that the Home Assistant backend has gone away and pause the WebView for the outage instead of hammering it with reconnect traffic that gets the panel rate-limited or IP-banned.

- State machine `HEALTHY → SUSPECT (20 s grace) → PAUSED (WebView paused, TCP probe every 10 s) → HEALTHY (resume + reload)`; clock, probe and backoff timings are injected so it is unit-testable
- Also fires an immediate reload on screen wake if the last known state was unhealthy, covering a backend restart that happened while the tablet was asleep and in-process timers were frozen by Doze
- Published as its own `dashboard` binary sensor (`device_class: connectivity`, `entity_category: diagnostic`) — deliberately separate from device availability, since the panel itself stays healthy during a backend outage
- Toggle in Settings → Resilience

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

## Long-term / Exploratory

### Android TV Support
Run kite-aos on Android TV boxes as a large-screen Home Assistant dashboard — delivered as a dedicated `tv` build flavor rather than branching the existing tablet flow.

**Market context:** No reliable, HA-native kiosk dashboard exists for Android TV. The closest competitor (`HA TV Dashboard`, Play Store) is rated 2.6/5 and functionally broken as of 2025. Homey launched a TV app but requires the Homey ecosystem — not compatible with Home Assistant. Android TV holds 35% of the smart TV OS market; 1.1B smart TV households projected by 2026.

**Target hardware:** Android TV boxes with USB-A host port (NVIDIA Shield TV Pro, Xiaomi Mi Box S+, ONN 4K Pro, generic Android TV boxes). Chromecast with Google TV and Fire TV Sticks are out of scope (no USB-A / no USB host).

#### Build & Architecture

- New `tv` product flavor in `build-logic` convention plugin; shares all `domain-*` and `data-*` modules unchanged; diverges only at `presentation-feature-*` and `presentation-core-platform`
- TV detection at runtime via `PackageManager.hasSystemFeature(FEATURE_LEANBACK)` as a fallback guard inside `HostActivity`
- `android.software.leanback` declared as `required=false` in the manifest + Leanback launcher `intent-filter` so the app appears on the Android TV home screen
- `device_class` published via MQTT as `tv` instead of `tablet`

#### Category 1 — Direct Port (no logic changes)

All core features transfer unchanged:

- Home Assistant WebView kiosk (same `KioskWebView` + JS injection)
- `MqttService` — telemetry, HA Discovery, screen control commands
- Battery telemetry (TV boxes expose battery state API identically)
- Screen wake / dim / lock via `PowerManager` + `WindowManager`
- `MotionService` interface preserved — implementation swapped per flavor
- Onboarding flow (layout adapted, logic unchanged)
- Settings screen (layout adapted, logic unchanged)
- Kiosk lockdown (launcher replacement; mechanism differs on TV but result identical)

#### Category 2 — Adapted Features

**D-pad / Remote Navigation (replaces all touch)**
- `HostActivity` intercepts `KEYCODE_DPAD_*` and `KEYCODE_DPAD_CENTER`; translates to WebView scroll and click via `evaluateJavascript` — no touch events assumed
- JS focus manager injected on page load: sets `document.activeElement` traversal via Tab/arrow keys so HA dashboard cards receive proper focus rings
- All Compose UI components audited for `Modifier.focusable()` + visual focus indicators (10-foot UI scale)
- FAB hidden in TV flavor; swipe gestures suppressed

**USB Webcam Motion Detection (replaces CameraX front camera)**
- Primary path: Camera2 API with `FEATURE_CAMERA_EXTERNAL` (API 28+, official Android mechanism, no extra dependency)
  - Check `PackageManager.hasSystemFeature(FEATURE_CAMERA_EXTERNAL)` at runtime; skip `MotionService` camera binding if absent
  - Luma analysis pipeline identical to existing CameraX implementation — same `ImageAnalysis` callback interface
- Fallback path: [`AndroidUSBCamera`](https://github.com/jiangdongguo/AndroidUSBCamera) (UVC, Apache 2.0) for devices that expose USB camera via USB Host but not through Camera2
  - Risk: last release v3.3.3 (Feb 2023), 488 open issues — use only as fallback, not primary
- USB permissions requested at runtime via `UsbManager`; user prompted once on first attach
- If neither path is available: `MotionService` falls back to MQTT trigger mode (see below)

#### Category 3 — New TV-Specific Features

**MQTT Motion Fallback**
- Subscribe to a configurable MQTT topic (e.g. from a PIR sensor connected to Home Assistant)
- On message received: fire the same `MOTION_DETECTED` event that CameraX would fire — wakes screen, resets dim timer, publishes presence state
- Configured in Settings → Motion section; enabled automatically if no camera is detected at startup
- Allows motion-based screen wake even on devices with no USB webcam attached

**TV Remote Key Mapping**
- `HostActivity.dispatchKeyEvent` intercepts configurable remote keycodes and maps them to app intents
- Default mappings:
  - Long-press `KEYCODE_BACK` (1 s) → open Settings (with optional PIN gate)
  - `KEYCODE_MENU` → toggle control overlay
  - Configurable N-press combo on any button (default: 5× `KEYCODE_DPAD_CENTER`) → PIN-protected kiosk exit
- Configurable in Settings → Access section (same pattern as volume-button gesture on tablet)
- Key events consumed only for mapped combos; all others propagate normally to system / WebView

**Ambient Mode (Screensaver)**
- Reuses the existing `Screensaver` roadmap item (clock overlay, entity-status display, configurable timeout)
- On TV: dismisses on MQTT motion event or remote keypress (D-pad center) instead of camera motion
- TV-specific: fullscreen clock uses larger typography scaled for 10-foot viewing distance

---

## Content

### Demo Video
A short screen-recorded walkthrough of the main features:
onboarding → dashboard → motion wake → control drawer → settings → MQTT in HA.
