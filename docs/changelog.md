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
