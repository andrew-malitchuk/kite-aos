# presentation-core-application

The entry point and composition root for the Home Kiosk application.

## Overview
This module contains the `Application` class and the primary `AndroidManifest.xml`. It doesn't implement features itself; instead, it stitches together the data, domain, and presentation features into a single executable APK.

## Features
- **Global DI Hub**: Initializes Koin and loads all sub-module dependency graphs.
- **App Startup**: Manages the transition from the splash screen to the main UI.
- **Persistent Services**: Starts and manages the lifecycle of the `MqttService` and other platform background tasks.
- **Permission Management**: Centralized declaration of hardware and system permissions.
- **Form-Factor Selection**: Owns the app `BuildConfig` and the `formfactor` flavor dimension that produces mobile and Android TV builds.

## Key Files
- `YahkApplication.kt`: Bootstraps the app, DI, and system receivers.
- `di/AppModule.kt`: The master list of all Koin modules in the project.
- `AndroidManifest.xml`: The source of truth for app components and required permissions.
- `src/tv/AndroidManifest.xml`: app-module TV overlay — launcher banner + `MotionService` service type (leanback declarations live in `presentation-feature-host`).

## Build Configuration
This module uses the `dev.yahk.convention.application` plugin, which applies the `com.android.application` plugin and configures signing, SDK versions, and base Compose dependencies.

## Android TV
This module makes the app installable and launchable on Android TV via the `formfactor` flavor dimension.

- **`BuildConfig.IS_TV`**: The `formfactor` dimension sets this flag — `false` for `mobile`, `true` for `tv`. Combined flavor names look like `fossTv`, so per-dimension fields (`FLAVOR_formfactor`, `FLAVOR_distribution`) are used to test one dimension alone.
- **Early seeding**: `YahkApplication` sets `AppConfig.buildFlagIsTv = BuildConfig.IS_TV` before Koin starts, so a `tv` build behaves as TV even if the device fails to report the leanback feature (e.g. a sideloaded APK). Feature libraries read this through the injected `AppConfig` abstraction (in `presentation-core-platform`), never `BuildConfig` directly.
- **TV-aware startup**: The `BatteryReceiver` is skipped when `AppConfig.isTv` is true, since TV boxes have no battery.
- **`src/tv/AndroidManifest.xml`** (merged only into `tv*` variants):
    - Declares `android:banner="@drawable/tv_banner"` on `<application>`, required for the app to appear in the Android TV launcher's Apps row.
    - Redeclares `MotionService` with `foregroundServiceType="camera|specialUse"` to cover both the external/USB camera path and the camera-less (MQTT presence) path on Android 14+.
    - Carries only these `<application>`-level bits. The leanback declarations (`android.software.leanback` / touchscreen / camera / microphone `required="false"`) and the `LEANBACK_LAUNCHER` intent-filter live in the `presentation-feature-host` TV overlay (alongside `HostActivity`).
- **App-drawer enumeration**: The main manifest's `<queries>` block declares both `LAUNCHER` and `LEANBACK_LAUNCHER` categories so TV-only apps are enumerable by the kiosk launcher on API 30+.
