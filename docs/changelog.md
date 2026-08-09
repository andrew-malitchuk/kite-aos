# [2.0.0] - 2026-08-09

Kite AOS 2.0 brings the project to the living room: the kiosk now runs on **Android TV** boxes as a 10-foot, D-pad-driven Home Assistant dashboard from a single codebase, with full parity to the tablet build. This release also lands a pluggable motion-source pipeline and a user-selectable camera source — a USB-OTG webcam now works on phones and tablets too, not just Android TV.

## Added

### Android TV
- `formfactor` flavor dimension (`mobile`/`tv`) across the convention plugins → `gmsMobile`/`fossMobile`/`gmsTv`/`fossTv`; `BuildConfig.IS_TV` and a `tv` `versionCode` offset for Play multi-APK delivery
- `AppConfig` runtime form-factor detection (UI-mode / `FEATURE_LEANBACK` / seeded `BuildConfig.IS_TV`) surfaced to feature libraries
- Adaptive 10-foot UI: `FormFactor`, `LocalFormFactor`, `LocalWindowSizeClass`, `Theme.is10Foot` token up-scaling, and `Modifier.tvFocusRing` D-pad focus rings
- D-pad control-drawer access via a hidden directional combo in `HostActivity`; TV master/detail settings layout with single-focus-target list navigation
- Leanback launcher + banner (`tv` manifest overlays); `LEANBACK_LAUNCHER` added to `<queries>` for app-drawer enumeration (`ApplicationPlatformSourceImpl` now enumerates both LAUNCHER and LEANBACK_LAUNCHER, deduped by package)
- `DarkOverlay` screen state — a plain dark overlay stand-in for screen-off on Android TV, where the app cannot lock/power off the panel (`ScreenStateModel`/`ScreenStateResource`); `DevicePowerManager` wake/lock are no-ops on TV
- Home Assistant form-factor entity gating: `MqttConnectUseCase`/`TelemetryMqttSource` take a `model` (`tv`/`tablet`) reported in HA Discovery; on TV, brightness/screen/volume/battery entities are not registered and stale ones are cleaned up
- `material3-window-size-class` dependency

### Motion pipeline
- Pluggable `MotionSource` abstraction — `MotionService` no longer hardcodes CameraX; Camera2-external, UVC, and MQTT motion sources sit behind one interface, with a stride-aware `MotionAnalyzer` overload for padded YUV/NV21 frames
- USB-webcam motion sources: `Camera2ExternalMotionSource` → `UvcMotionSource` (libausbc) fallback → `MqttMotionSource` presence fallback, with `HeadlessGlSurfaceTexture` for the UVC surface requirement

### Camera source selection
- User-selectable camera source (`Auto`/`Front`/`Rear`/`External`) on every form-factor: `CameraSourceModel` domain model and a `camera.pb` Proto DataStore preference (`CameraPreferenceSource`, serializer, mapper, storage)
- `GetCameraSourceUseCase` / `SetCameraSourceUseCase` / `ObserveCameraSourceUseCase` and `ConfigureRepository` camera-source methods
- Settings selector to pick the camera source; localized strings (EN + UK)
- `MotionSourceFactory.create(choice)` honors the preference: `Auto` picks the highest-priority available source, `Front`/`Rear` force the built-in lens, `External` forces a USB/UVC webcam, with fallback to `Auto` when the forced source is unavailable

## Changed
- MQTT publishes `device_class = tv` on the `tv` flavor; screensaver reused as the TV ambient mode
- Android TV documentation promoted from design plan to as-built reference ([android-tv.md](android-tv.md))
- Bumped `versionName` to `2.0.0` (`versionCode` 7)

# [1.1.0] - 2026-06-13

This release adds screensaver overlay, MJPEG streaming, auto-reboot scheduler, analytics infrastructure, and expands settings with five new preference categories.

## Added
- Analytics infrastructure with pluggable provider pattern (console + Firebase for gms flavor)
- Screensaver, auto-reboot, streaming, reduce-motion, and webview-refresh preferences
- Domain models and repository contracts for new features
- Use cases for screensaver, streaming, auto-reboot, screen state, and webview-refresh
- MQTT telemetry extended with screensaver, FAB, and camera URL commands
- Auto-reboot scheduler, MJPEG streaming server, and network resolver to platform
- Screensaver overlay with clock component to main dashboard
- Settings expanded with streaming, screensaver, auto-reboot, and webview-refresh configuration
- Analytics, new modules, and updated services wired into app composition root
- Project documentation, architecture guides, and MkDocs configuration

## Fixed
- Guard Application.onCreate against GeckoView child processes

## Changed
- Remove watchdog recovery system from main feature
- Update F-Droid store listing description
- Remove GEMINI.md documentation files from all modules

# [1.0.0] - 2026-05-27

This release marks the first major version of Kite AOS, introducing pluggable WebView engine support, automatic return, Home Assistant network discovery, expanded MQTT telemetry, and Android 7.1 (API 25) compatibility for the GMS flavor.

## Added
- Pluggable WebView engine support (AndroidWebView / GeckoView) with Proto DataStore preferences
- Auto-return preference source and implementation
- Home Assistant network scanner and host discovery
- GetWebEngineUseCase / SetWebEngineUseCase across all layers
- DiscoverHomeAssistantUseCase, ObserveNetworkStatus, SetAutoReturn use cases
- MQTT network state and watchdog telemetry use cases
- Chrome and Firefox browser icon assets

## Fixed
- Lower minSdk to 25 (Android 7.1) for GMS flavor — closes #7
- Scope GeckoView minSdk constraint to FOSS flavor only; add GMS manifest override
- Fallback to AndroidWebView at runtime when GeckoView is selected on API < 26
- Add dontwarn rules for GeckoView bundled Kotlin annotations (R8 fossRelease build)

## Changed
- Update F-Droid short_description.txt

# [0.0.3] - 2026-05-06

This release adds full bidirectional MQTT control with Home Assistant discovery, WebRTC support, Firebase Crashlytics integration, and a new onboarding microphone permission step.

## Added
- Add RECORD_AUDIO permission step to onboarding
- Publish WebView URL to MQTT broker on page load
- Extend MqttService with bidirectional command routing
- Add domain use cases and repository for MQTT device control
- Expand MQTT data source for bidirectional control
- Add MQTT HA discovery config models for device control
- Add Firebase Crashlytics integration

## Fixed
- Add WebRTC permissions and hardware acceleration

## Changed
- Rename convention plugin package to convention
- Remove unused data-network modules from build
- Update documentation
- Upgrade Gradle to 9.3.1 and update version catalog
- Fix indentation in UI components
