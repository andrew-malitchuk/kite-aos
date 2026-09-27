# presentation-core-platform

The platform and hardware engine for the Home Kiosk application.

## Features
- **Intelligent Motion Guard**: Detects movement from a selectable camera source, waking the screen and dimming it when idle. The `CameraSourceModel` preference (`Auto`/`Front`/`Rear`/`External`) chooses the source — see [Camera Source Selection](#camera-source-selection). On Android TV (no built-in camera) the same guard runs off a USB webcam or an MQTT presence topic — see [Android TV](#android-tv).
- **Feedback Protection**: Specialized "blindness" logic prevents the service from being triggered by its own light changes.
- **Kiosk Security**: Integration with Android Device Administration for programmatic screen locking.
- **Background MQTT**: Persistent foreground service for maintaining MQTT connectivity.
- **Battery Tracking**: Real-time battery status reporting via MQTT.
- **Device Telemetry** (`@since 2.1.0`): `DeviceTelemetryProvider` samples uptime, app version, LAN IP address, and device-wide RAM usage on a timer for the companion diagnostics sensors — all permission-free reads, published to Home Assistant every 60 s.
- **Wake & WiFi Lock** (`@since 2.2.0`): `LockManager` holds a CPU wake lock for the life of the process and a WiFi lock through the screen-off window, so an idle panel keeps its MQTT session alive instead of silently dropping off overnight. The CPU lock is unconditional; the WiFi lock is user-disableable, because some budget WiFi chipsets wedge their driver if kept out of power-save for hours.
- **Crash Auto-Relaunch** (`@since 2.2.0`): `CrashRelaunchHandler` schedules a one-shot alarm to relaunch the kiosk after an uncaught crash, then lets the process die — no overlay permission or surviving service required. A mandatory rate limiter (`CrashDiagnosticsStore`) suppresses the relaunch after 3 crashes in 10 minutes so a startup crash loop can't flatten the battery on a wall-mounted panel.
- **Memory Recovery & Scheduled Reload** (`@since 2.2.0`): `MemoryRecoveryCoordinator` reloads the dashboard on system memory pressure, holding the request until the screensaver is up so nobody watching the panel sees it blank. `WebViewReloadScheduler` optionally arms a daily reload via `AlarmManager`, re-arming itself after each firing so the schedule survives Doze and DST shifts.
- **Dashboard Connection Monitor** (`@since 2.2.0`): `DashboardConnectionMonitor` pauses the WebView when the Home Assistant backend goes unreachable — stopping its ~1/second WebSocket retry storm, which can otherwise get the panel rate-limited or IP-banned — and resumes it once the backend answers again. Reported to Home Assistant as its own `dashboard` binary sensor, independent of device availability.

## Requirements
- **Permissions**:
    - `CAMERA`: Required for motion detection.
    - `WRITE_SETTINGS`: Required for managing screen brightness.
    - `POST_NOTIFICATIONS`: Required for foreground service visibility (Android 13+).
- **Device Setup**: The application must be enabled as a **Device Administrator** in system settings for the remote lock feature to function.

## Internal Structure
- `core/extension/`: Useful Android platform extensions (e.g., `Context.openAppLanguageSettings`).
- `core/helper/`: System interaction helpers (e.g., `DevicePowerManager`, `DeviceTelemetryProvider`).
- `source/analyzer/`: Mathematical and frame analysis logic (e.g., `MotionAnalyzer`).
- `source/command/`: `RemoteCommandBus`, the seam that carries motion, drawer, reload, home-reset, interaction and clear-cache events raised outside the composition.
- `source/connection/`: `DashboardConnectionMonitor` and the pure `DashboardConnectionMachine` state machine behind the connection guard.
- `source/diagnostics/`: `CrashRelaunchHandler`, `CrashDiagnosticsStore`, `CrashRelaunchSettingMirror`, and `MemoryRecoveryCoordinator`.
- `source/power/`: `LockManager`, the CPU wake lock and WiFi lock owner.
- `source/scheduler/`: `WebViewReloadScheduler`, the `AlarmManager`-backed daily reload.
- `source/receiver/`: Broadcast receivers for system events, including `KioskRelaunchReceiver` and `WebViewReloadReceiver`.
- `source/service/`: Foreground services for long-running background tasks.

## Camera Source Selection
`MotionService` no longer hardcodes the front CameraX camera. The persisted `CameraSourceModel` preference (`Auto`/`Front`/`Rear`/`External`, `@since 1.4.0`) drives which `MotionSource` is used, via `MotionSourceFactory.create(choice)`. The service observes the choice and re-selects the source live when it changes.

- **`Auto`**: the highest-priority *available* source wins (the historical behaviour) — Camera2-external → UVC → MQTT → no-op on TV; CameraX on mobile.
- **`Front` / `Rear`**: force a built-in-lens source (the lens is passed separately via `MotionSourceConfig.lens`).
- **`External`**: force a USB/UVC (external) source. Because the UVC engine ships on every variant, this works on a phone or tablet with a USB-OTG webcam too, not only on TV.
- A forced choice with no matching available source falls back to the `Auto` selection, so the screen never goes dark waiting on a camera that isn't there.

## Survivability (`@since 2.2.0`)
A wall-mounted dashboard is judged on uptime, so this module carries a small set of features aimed purely at keeping the panel alive and reachable overnight, unattended.

- **Wake & WiFi Lock**: `LockManager` acquires a `PARTIAL_WAKE_LOCK` for the life of the process and a WiFi lock while the screen is off, so aggressive OEM power management can't park the WiFi radio or deprioritise the process and silently kill the MQTT session. The WiFi lock can be switched off per-user for WiFi chipsets that misbehave under sustained full-power mode.
- **Crash Auto-Relaunch**: an uncaught crash schedules a one-shot alarm that relaunches the app after the process dies, so a panel recovers from a crash on its own. A built-in rate limiter (3 crashes / 10 min) stops a boot-looping crash from being relaunched forever.
- **Memory Recovery & Scheduled Reload**: the dashboard reloads itself when Android reports memory pressure (deferred until the screensaver is up, so nobody sees a blank page), and can optionally reload once a day on a schedule that survives Doze and DST changes. Both kinds of reload keep the current page — only the inactivity reset returns to the home URL.
- **Dashboard Connection Monitor**: pauses the WebView when the Home Assistant backend stops responding, so its frontend's rapid WebSocket retries don't get the panel rate-limited or banned by a reverse proxy, and resumes it automatically once the backend is healthy again. Surfaced to Home Assistant as its own `dashboard` binary sensor, independent from whether the device itself is online.

`RemoteCommandBus` carries the events these features raise outside the composition — `reload` (scheduled/memory recovery), `homeReset` (inactivity reset) and `interaction` (D-pad presses on Android TV, which never reach the Compose pointer pipeline) — over to the `presentation-feature-main` screen that owns the mounted engine.

## Android TV
The `tv` build flavor turns an Android TV box into the same kiosk, but TV boxes have no built-in camera, so presence detection is sourced from external hardware.

- **Form-Factor Detection**: `AppConfig` (`@since 1.2.0`) centralises "is this a TV device?" for the whole presentation layer, because feature libraries cannot read the app module's `BuildConfig`. `isTv` is `true` when the system reports a television UI mode, declares the leanback feature, or the app seeded the TV build flag. `MotionService`, `MqttService`, and `DevicePowerManager` branch on it.
- **Motion Sources (by priority)**: For the `Auto` choice, the active source is picked at runtime from the first available of:
    - **Camera2 external webcam** (primary): a USB webcam surfaced by the platform as a standard Camera2 external camera. No extra dependency needed.
    - **UVC USB webcam** (fallback): a USB webcam that Camera2 does not expose, driven through the `libausbc` UVC engine (a headless off-screen GL surface satisfies the engine's mandatory-preview requirement).
    - **MQTT presence** (fallback): for camera-less boxes — presence pulses from a configurable MQTT/PIR topic become motion events. No video, so streaming is inactive.
- **Reused Pipeline**: Whichever camera source is active, its frames flow through the exact same luma-based `MotionAnalyzer` and the same MJPEG streaming server (`/stream.mjpg`, `/snapshot.jpg`) as the mobile front camera. The webcam sources feed padded YUV/NV21 buffers through `MotionAnalyzer`'s stride-aware `analyze(...)` overload (`@since 1.2.0`), which shares the exact luma scoring of the CameraX path.
- **MQTT service on TV**: `MqttService` reports the device class as `"tv"` (not `"tablet"`) to Home Assistant discovery, skips the brightness/screen-state observers (the panel is not app-controllable), and bridges inbound MQTT motion pulses onto `RemoteCommandBus.motion()` so `MqttMotionSource` can turn them into motion events — the PIR / HA-automation presence fallback when no camera is attached.
- **Power management on TV**: `DevicePowerManager.wakeUp()` and `lockDevice()` are no-ops on TV — the panel is HDMI-CEC driven and device-admin locking is unavailable. Instead the app uses the `DarkOverlay` screen state as a screen-off stand-in (and only when a camera source is active, so it can be dismissed locally).

### TV Internal Structure
- `source/config/`: `AppConfig` form-factor detection (`AppConfigImpl` is a Koin `@Single`).
- `source/motion/`: `Camera2ExternalMotionSource` and `UvcMotionSource` (+ `HeadlessGlSurfaceTexture`) live in `src/main`, guarded at runtime by `isAvailable`; `MqttMotionSource` lives in `src/tv` (compiled into the `tv` flavor only). Flavor gating is by source-set; `MotionSourceFactory` does the runtime selection.

### TV Requirements
- A USB webcam (UVC) on the box's USB host port, **or** an MQTT presence source, for motion detection.
- The `androidusbcamera` (`libausbc` + `libuvc`, JitPack) UVC engine is a plain `implementation` on **every** variant (not `tvImplementation`), with its demo transitives excluded — so the `External` camera source is available to phones/tablets with a USB-OTG webcam as well as TV.

## Service Management
The services in this module are typically started and stopped based on user configuration managed by the `domain` layer. They are designed to be resilient and handle lifecycle transitions (like screen on/off) gracefully.
