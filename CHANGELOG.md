# [1.0.0] - 2026-05-27

This release marks the first major version of Kite AOS, introducing pluggable WebView engine support, automatic return, Home Assistant network discovery, and expanded MQTT telemetry for network and watchdog state.

## Added
- Implement web engine preferences, auto-return, HA discovery, and WebView engine abstraction

## Changed
- Update short_description.txt

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
