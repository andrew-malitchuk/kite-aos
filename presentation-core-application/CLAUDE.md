# Module: presentation-core-application

## Overview
This module serves as the **Composition Root** and primary entry point for the "kite-aos" Android application. It is responsible for orchestrating the initialization of all other modules, configuring global dependency injection, and defining the core application structure in the Android Manifest.

## Responsibilities
*   **Application Lifecycle**: Implements the `YahkApplication` class to handle process creation and global state initialization.
*   **DI Orchestration**: Centralizes the Koin dependency injection configuration by including modules from all layers (Data, Domain, and Presentation).
*   **System Integration**: Declares all necessary Android permissions and registers global components like foreground services and broadcast receivers.
*   **Visual Identity**: Defines the base application themes, including the Splash Screen and immersive kiosk mode styles.
*   **Service Bootstrapping**: Automatically launches critical background services (like MQTT and Motion detection) upon application start.

## Architecture
As the top-level module, it sits above all other layers. It does not contain business logic but rather "glues" the system together:
*   **`YahkApplication`**: The hub where Koin is started and system-level observers (like battery tracking) are registered.
*   **`appModule`**: A declarative Koin module that uses the `includes()` DSL to compose the entire project's dependency graph.
*   **Manifest**: Manages the application's security model (Permissions) and component definitions.

## Key Components
*   **`presentation.core.application.YahkApplication`**: The central application controller.
*   **`presentation.core.application.di.AppModule`**: The master registry for all dependencies in the system.

## Android TV
This module owns the app-level `BuildConfig` and the source sets that make the app installable and launchable on Android TV.

*   **`formfactor` flavor dimension**: Defines `BuildConfig.IS_TV` — `false` for the `mobile` flavor, `true` for the `tv` flavor. Combined flavor names look like `fossMobile` / `fossTv`, so per-dimension fields (`BuildConfig.FLAVOR_formfactor`, `BuildConfig.FLAVOR_distribution`) are used to test one dimension in isolation.
*   **Early TV seeding**: `YahkApplication.onCreate` sets `AppConfig.buildFlagIsTv = BuildConfig.IS_TV` *before* Koin is started, so the flag is available before the DI graph is consumed. This lets a `tv`-flavored build behave as TV even when a device fails to report the leanback feature (e.g. a sideloaded APK on hardware that does not advertise leanback). Feature libraries cannot read this `BuildConfig` directly — they consume the value through the injected `AppConfig` abstraction (in `presentation-core-platform`), which combines the compile-time flag with runtime detection.
*   **TV-aware bootstrapping**: `BatteryReceiver` is only registered when `!appConfig.isTv`, since TV boxes have no battery and the sensor would report meaningless values (its Home Assistant entity is also not registered on TV).
*   **`src/tv/AndroidManifest.xml` overlay** (merged only into `tv*` variants, leaving mobile builds untouched):
    *   Adds `android:banner="@drawable/tv_banner"` on `<application>` — required for the app to appear in the Android TV launcher's "Apps" row; a leanback-launcher activity with no banner is silently hidden.
    *   Overrides `MotionService`'s `foregroundServiceType` to `camera|specialUse` via `tools:replace`. On TV the motion source is chosen at runtime (Camera2 external/USB camera, UVC webcam, an MQTT presence topic, or no source), so declaring both types keeps the camera path legal on Android 14+ while covering the camera-less case via `specialUse` (with a `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` describing the use).
    *   This app-module overlay carries only the `<application>`-level bits (banner + `MotionService` service type). The leanback support declarations (`android.software.leanback` / touchscreen / camera / microphone `required="false"`) and the `LEANBACK_LAUNCHER` intent-filter live in the **`presentation-feature-host`** `src/tv/AndroidManifest.xml` (where `HostActivity` is declared), so the app shows on the Android TV home screen without excluding non-TV hardware.
*   **`<queries>` in the main manifest**: Declares both `LAUNCHER` and `LEANBACK_LAUNCHER` categories so TV-only apps (visible only under leanback) are enumerable by the kiosk application-drawer launcher on API 30+ without `QUERY_ALL_PACKAGES`.

## Dependencies
This module depends on every feature and implementation module in the project to fulfill its role as the composition root. It utilizes the `dev.yahk.convention.application` plugin for standardized build configuration.
