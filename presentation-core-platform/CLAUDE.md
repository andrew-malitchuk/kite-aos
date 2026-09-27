# Module: presentation-core-platform

## Overview
This module provides low-level platform integration and system services required by the presentation layer. It handles device-specific capabilities that fall outside standard UI logic, such as motion detection, power management, and administrative policies.

## Responsibilities
*   **Motion Detection**: Implements `MotionService`, which analyzes frames from a selectable camera source (via `MotionSourceFactory`) to detect user presence or movement. The source follows the user's `CameraSourceModel` preference (`Auto`/`Front`/`Rear`/`External`, `@since 1.4.0`) and is re-selected live when the preference changes.
*   **Power & Display Management**: Automatically manages screen brightness, wakes the device on motion, and enforces inactivity-based locking.
*   **Device Administration**: Provides `ApplicationDeviceAdminReceiver` to handle administrative privileges required for kiosk-mode features like programmatically locking the screen.
*   **MQTT Lifecycle**: Manages the MQTT connection as a foreground service, ensuring telemetry can be reported even when the UI is not active.
*   **Telemetry**: Monitors system events like battery level changes and reports them to the telemetry system, and samples the low-frequency companion diagnostics (uptime, app version, LAN IP, device RAM usage) on a timer.
*   **System Navigation**: Contains extensions to simplify interaction with Android system settings (e.g., language settings).

## Architecture
This module sits at the base of the presentation layer, interacting directly with Android framework components:
*   **Foreground Services**: `MotionService` and `MqttService` run as foreground services to maintain active background processing regardless of the application's visibility.
*   **Broadcast Receivers**: Handles system events related to device administration (`ApplicationDeviceAdminReceiver`) and battery status (`BatteryReceiver`).
*   **CameraX Integration**: Uses `ImageAnalysis` for real-time frame processing with minimal power consumption.

## Android TV
This module carries the platform-level support for the `tv` build flavor. TV boxes have no built-in camera, so motion detection is sourced differently, and shared feature code must be able to branch on form-factor at runtime.

*   **Form-factor detection (`AppConfig`)**: `AppConfig` (interface, `@since 1.2.0`) centralises "is this a TV device?" detection, because feature libraries cannot read the application module's `BuildConfig`. `AppConfigImpl` (`@Single(binds = [AppConfig::class])`) reports `isTv = true` when `UiModeManager.currentModeType == UI_MODE_TYPE_TELEVISION`, **or** `PackageManager` declares `FEATURE_LEANBACK`, **or** the app module seeded `AppConfig.buildFlagIsTv = true` from its own `BuildConfig.IS_TV`. Mobile and TV never coexist on one device, so the permissive OR is safe. `MotionService`, `MqttService`, and `DevicePowerManager` consume `AppConfig` to branch behaviour (foreground-service type, TV vs mobile motion path, etc.).
*   **Motion sources & the camera choice**: `MotionSourceFactory.create(choice)` selects a `MotionSource` from the persisted `CameraSourceModel` preference (`Auto`/`Front`/`Rear`/`External`, observed by `MotionService` and re-selected live on change). `Auto` keeps the legacy "highest-priority available" behaviour — `Camera2ExternalMotionSource` (40) → `UvcMotionSource` (30) → `MqttMotionSource` (20) → `NoOpMotionSource` on TV; `CameraXMotionSource` (50) on mobile. `Front`/`Rear` force the `CameraKind.BUILT_IN` source (lens passed via `MotionSourceConfig.lens`); `External` forces the highest-priority `CameraKind.EXTERNAL` source; a forced-but-unavailable choice falls back to the `Auto` selection. `MqttMotionSource` lives in `src/tv/` (compiled into the `tv` flavor only); `CameraXMotionSource`, `Camera2ExternalMotionSource`, `UvcMotionSource`, and `HeadlessGlSurfaceTexture` all live in `src/main/` and are guarded at runtime by `isAvailable` — so the USB/UVC "External" path works on phones/tablets with USB-OTG too, not just TV. All sources wrap frames as the shared `MotionFrame`, so the same luma-based `MotionAnalyzer` pipeline and MJPEG streaming server (`GET /stream.mjpg`, `GET /snapshot.jpg`) are reused unchanged.
    *   **`Camera2ExternalMotionSource`** (primary): drives a USB/external webcam that the platform surfaces as a Camera2 `LENS_FACING_EXTERNAL` device (`FEATURE_CAMERA_EXTERNAL`, API 28+), streaming `YUV_420_888` frames via `ImageReader`. Preferred because it needs no third-party dependency.
    *   **`UvcMotionSource`** (fallback): drives a UVC USB webcam that Camera2 does *not* expose, via the AndroidUSBCamera (`libausbc`) engine, wrapping its NV21 preview frames.
    *   **`MqttMotionSource`** (fallback): for camera-less boxes — turns presence pulses from a configurable MQTT/PIR topic (via `RemoteCommandBus.motion`) into synthetic motion frames. Produces no video, so MJPEG streaming is inactive under this source.
    *   **`HeadlessGlSurfaceTexture`**: an off-screen, self-draining GL `SurfaceTexture` handed to `libausbc` as its mandatory preview target. `libausbc`'s UVC `openCamera` requires a non-null render surface and crashes *uncatchably* on its own camera thread when given a null one; the texture keeps the engine's preview loop alive (draining buffers so the NV21 callback never stalls) while displaying nothing.
*   **Stride-aware `MotionAnalyzer` (`@since 1.2.0`)**: the webcam sources (Camera2 external / UVC) hand `MotionAnalyzer` padded YUV/NV21 buffers, so `analyze(image: ImageProxy, sensitivity)` (the flat CameraX path) is joined by an overload `analyze(yPlane: ByteBuffer, rowStride, pixelStride, width, height, sensitivity)`. It samples on a stride-aware 2D grid — handling both contiguous NV21 (`rowStride == width`, `pixelStride == 1`) and row-padded `YUV_420_888` (`rowStride > width`, padding skipped) — and shares the same luma-difference scoring (`scoreFrame`) as the CameraX overload, so sensitivity is comparable across sources.
*   **`MqttService` on TV**: reports the device class as `"tv"` (not `"tablet"`) to Home Assistant discovery (`mqttConnectUseCase(model = ...)`); skips the screen-state `BroadcastReceiver` and brightness `ContentObserver` (and their initial-state publishes), since the TV panel's brightness/screen and audio-over-HDMI are not app-controllable; and, when connected, bridges inbound MQTT motion pulses (`ObserveMqttMotionCommandUseCase`) onto `RemoteCommandBus.emitMotion()`. That bus feeds `MqttMotionSource`, giving a PIR / HA-automation presence fallback for camera-less boxes. The bridge runs on TV only — mobile uses a camera and has no bus consumer.
*   **`DevicePowerManager` on TV**: constructed with `isTv` (from `AppConfig`), so `wakeUp()` and `lockDevice()` become no-ops — the panel is HDMI-CEC driven (a `SCREEN_BRIGHT_WAKE_LOCK` can't physically turn it on) and Android TV exposes no Device Administrator enrollment for `lockNow()`. Instead of locking, `MotionService` emits the `ScreenStateModel.DarkOverlay` screen state as a screen-off stand-in — and only when the active source `isCameraBased`, so the overlay is always locally dismissable and can't leave the screen stuck dark.
*   **UVC dependencies (`build.gradle.kts`)**: `androidusbcamera-libausbc` and `androidusbcamera-libuvc` (v3.2.7, JitPack) are added as `implementation` on **every** variant (not `tvImplementation`), so the "External" camera choice works on mobile as well as TV. `libausbc`'s bundled demo transitives are excluded (`com.gyf.immersionbar`, `com.zlc.glide`/webpdecoder, Glide, `mmkv` — several of which resolve nowhere post-JCenter). `libuvc` is declared explicitly because `libausbc` leaks `com.serenegiant.usb.UsbControlBlock` in its public callback API yet scopes `libuvc` as `implementation`, keeping that type off the compile classpath. `libausbc`'s bundled manifest declares `android.hardware.usb.host` as required; the app manifest overrides it to `required="false"` (with `tools:replace`) so the app stays installable on devices without USB host.

## Key Components
*   **`MotionService`**: The core service orchestrating motion detection. It resolves the active `MotionSource` from the user's `CameraSourceModel` choice via `MotionSourceFactory`, delegates frame analysis to `MotionAnalyzer`, and power management to `DevicePowerManager`.
*   **`MotionAnalyzer`**: Encapsulates the mathematical logic for detecting motion from camera frames using luma analysis and scoring. Exposes two `analyze(...)` overloads — one for CameraX `ImageProxy` frames and a stride-aware one for padded YUV/NV21 webcam buffers — that share the same scoring core.
*   **`MotionSourceFactory`**: Selects the `MotionSource` for the current `CameraSourceModel` choice and runtime capability (`isAvailable`); flavor gating is by source-set, selection is at runtime.
*   **`DevicePowerManager`**: A helper class that abstracts interactions with system services for managing brightness, wake locks, and device security policies.
*   **`MqttService`**: A reactive service that observes the MQTT configuration and manages the connection state automatically.
*   **`ApplicationDeviceAdminReceiver`**: Entry point for device administrator policies, primarily used for the `force-lock` capability.
*   **`BatteryReceiver`**: A broadcast receiver that calculates battery percentage and triggers MQTT telemetry updates.
*   **`CrashRelaunchHandler` / `CrashDiagnosticsStore`** (`@since 2.2.0`): Crash recovery with a mandatory boot-loop rate limiter.
*   **`WebViewReloadScheduler` / `MemoryRecoveryCoordinator`** (`@since 2.2.0`): Scheduled and pressure-driven dashboard reloads.
*   **`DashboardConnectionMonitor` / `DashboardConnectionMachine`** (`@since 2.2.0`): Pauses the WebView while the backend is unreachable; the machine is pure and testable.
*   **`DeviceTelemetryProvider`** (`@since 2.1.0`): Samples the four companion diagnostic values published to Home Assistant. All are permission-free reads, which is what makes polling them acceptable instead of wiring four observers. `versionName` is resolved once and cached (it cannot change without a process restart). RAM usage is **device-wide** (`ActivityManager.MemoryInfo`), not per-process, because Android's low-memory killer decides from free system memory — so that is the number that actually predicts the blank-page renderer kill a long-running dashboard hits.

### Companion Telemetry & Discovery Lifecycle (`@since 2.1.0`)
`MqttService` gains two peer coroutines inside its connected block, so both are cancelled
automatically when the configuration changes or the connection drops:

*   **`publishCompanionTelemetry()`** publishes a `DeviceTelemetryProvider` sample every 60 s,
    immediately before the first delay so Home Assistant is populated on connect rather than showing
    unknown sensors for the first minute. A plain coroutine loop is deliberate here, unlike the
    scheduled work that must survive Doze via `AlarmManager`: being frozen while the device sleeps is
    correct for telemetry — it reports on a *running* app, and the availability topic already tells
    Home Assistant when the panel has stopped reporting.
*   **`observeClearCacheCommand()`** bridges Home Assistant `clear_cache` button presses onto
    `RemoteCommandBus.emitClearCache()`. The cache belongs to whichever engine the Main feature has
    mounted, which the service has no handle on, so the bus is the seam — the same route the MQTT FAB
    command already takes.
*   **`observeRemoteCommands()`** (`@since 2.2.0`) bridges the shared command topic
    (`{clientId}/command/set`) onto the same bus, translating each decoded `RemoteCommandModel`
    into one `emit*` call. The service handles none of them itself — navigation, history and script
    evaluation all act on the WebView the Main feature owns. Keeping this a pure translation step is
    what will let a second transport (a local HTTP control API) reuse the behaviour instead of
    reimplementing it. `navigate_home` maps onto `emitHomeReset()` and `clear_cache` onto
    `emitClearCache()`, so the remote commands and their dedicated entities share one code path.

On the **disabled** branch, `MqttPurgeDiscoveryUseCase` runs *before* `MqttDisconnectUseCase`.
Order matters: discovery configs are retained messages, so disconnecting first would strand every
entity in Home Assistant with nothing left to update it, leaving the user to delete them by hand.

### Survivability (`@since 2.2.0`)
A wall-mounted dashboard is judged on uptime. Each piece here exists because a specific class of
Android behaviour will otherwise take the panel down silently, overnight, with nobody watching.

**Crash auto-relaunch (2.2)** — `CrashRelaunchHandler` is a `Thread.UncaughtExceptionHandler` that
schedules a one-shot `AlarmManager` relaunch and then lets the process die. The alarm fires from
*outside* the dead process, so this needs no overlay permission, no device-owner privilege and no
surviving service. Three constraints shape the implementation:
*   **The rate limiter is mandatory, not optional.** A deterministic startup crash plus an
    unconditional relaunch is a boot loop that flattens the battery and leaves a wall-mounted panel
    unrecoverable without physically attaching ADB. `CrashDiagnosticsStore` suppresses the relaunch
    after `MAX_CRASHES_IN_WINDOW` (3) crashes inside `CRASH_WINDOW_MS` (10 min).
*   **`SharedPreferences`, not DataStore.** This is the only preference in the app not on Proto
    DataStore. The crash path runs in a process being torn down, where a `suspend` DataStore write
    is not guaranteed to complete; `SharedPreferences.commit()` writes on the calling thread. For
    the same reason the user's on/off setting is mirrored ahead of time by
    `CrashRelaunchSettingMirror` rather than read at crash time.
*   **The previous handler is always chained**, so Firebase Crashlytics (gms) still receives the
    crash. `install()` is therefore called *after* `CrashlyticsInitializer.init()`.

**Memory recovery & scheduled reload (2.3)** — two triggers, one reload.
*   `MemoryRecoveryCoordinator` turns `Application.onTrimMemory` into a reload, but **gates it on
    the screensaver being up**. Reloading blanks the page, so when pressure arrives while somebody
    is looking at the panel the request is held and executed the moment it next idles.
*   `WebViewReloadScheduler` arms the optional daily reload. **It must use `AlarmManager`, never a
    coroutine `delay` or a `Handler`**: in Doze, in-process timers do not fire, and the overnight
    idle window is exactly when the reload is both most needed and least disruptive. A timer-based
    implementation looks correct in testing and silently never runs in production.
*   The alarm is one-shot and re-armed by `WebViewReloadReceiver` after each firing. Recomputing the
    *next future* occurrence each time is what keeps the schedule correct across a DST shift, where
    a fixed 24-hour repeat would drift by an hour.
*   Path preservation needs no machinery: a reload re-requests the current page, so the dashboard
    stays where it was. (Only the inactivity reset deliberately discards the path.)

**Dashboard connection monitor (2.4)** — `DashboardConnectionMonitor` +
`DashboardConnectionMachine`, `HEALTHY → SUSPECT → PAUSED → HEALTHY`.
*   **Why pause at all:** when Home Assistant restarts, its frontend retries the WebSocket about
    once a second, indefinitely. Behind a reverse proxy or intrusion-prevention layer that is
    indistinguishable from an attack — the panel gets rate-limited or IP-banned and then stays
    broken *after* Home Assistant is healthy again. Pausing stops the storm at its source.
*   The `SUSPECT` grace period (20 s) exists so a brief hiccup never causes a pause/resume cycle.
*   `DashboardConnectionMachine` is deliberately free of Android types, coroutines and I/O — clock
    and probe are parameters — so the timing-dependent logic is unit-testable with a fake clock.
    This is the one piece of the survivability work worth real test coverage, because its failure
    mode (a panel stuck paused) is silent.
*   The probe is a bare TCP connect, not an HTTP request: it is the cheapest "something is
    listening" signal and adds no request to the rate limiter that caused the outage to persist.
*   `onScreenWake()` forces recovery when the panel slept while unhealthy — Doze freezes the
    in-process ticker, so an overnight outage would otherwise leave the panel paused after waking.
*   State is published to Home Assistant as its own `dashboard` binary sensor, **not** folded into
    device availability: the panel is healthy during a backend outage, and marking the whole device
    unavailable would hide its own working controls exactly when someone is diagnosing the server.

### Interaction (`@since 2.2.0`)
`RemoteCommandBus` carries three further channels for behaviour raised outside the composition:
`reload` (scheduled/memory recovery), `homeReset` (inactivity reset) and `interaction`. The last is
needed because Android TV D-pad input is seen by `HostActivity`, above the navigation graph, and
never reaches the Compose pointer pipeline that reports touches on mobile.

## Dependencies
*   **CameraX**: For frame analysis.
*   **Lifecycle Service**: For lifecycle-aware foreground processing.
*   **Domain Use Cases**: Orchestrates business logic for motion emission and MQTT operations.
