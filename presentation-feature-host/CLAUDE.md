# Module: presentation-feature-host

## Overview
This module serves as the primary entry point and host for the entire application. It contains the main activity that bootstraps the UI, manages system-level configurations, and orchestrates the initial navigation flow.

## Responsibilities
*   **Application Bootstrapping**: `HostActivity` serves as the single activity in the app's Single Activity Architecture.
*   **Splash Screen Management**: Handles the display and dismissal of the splash screen, including custom exit animations and synchronization with initial data loading.
*   **Initial Route Selection**: Determines whether to show the Onboarding flow or the Main dashboard based on the user's completion status.
*   **System UI & Interaction**: Enforces immersive mode (hiding status/navigation bars) and globally blocks the back gesture/button to maintain kiosk integrity.
*   **Auto-Start**: Contains the `BootReceiver` to automatically launch the kiosk activity when the device finishes booting.
*   **Theme Observation**: Observes the global theme preference and applies it to the entire application host.

## Architecture
This module acts as the shell for the application's Compose-based UI. It integrates the navigation implementation and serves as the bridge between the Android Activity lifecycle and the Composable world.

### Key Components
*   **`presentation.feature.host.source.host.HostActivity`**: The central coordinator for the app's lifecycle and UI hosting.
*   **`presentation.feature.host.source.receiver.BootReceiver`**: Ensures the kiosk is always running by starting the activity on system boot.

## Android TV

This module also serves as the host on Android TV. The TV-specific behaviour is resolved at the host root so downstream features stay form-factor agnostic.

*   **Form-Factor Resolution**: `HostActivity` computes `formFactor = if (appConfig.isTv) FormFactor.TV else FormFactor.MOBILE` and `calculateWindowSizeClass(activity)`, then publishes both above `AppTheme` via `CompositionLocalProvider(LocalFormFactor provides ..., LocalWindowSizeClass provides ...)`. This lets 10-foot scaling and TV input branches work without prop-drilling. On mobile both default to MOBILE, so behaviour is unchanged.
*   **D-pad "Settings Unlock"**: There is no FAB on TV, so the control drawer is opened by a hidden Konami-style D-pad sequence (Up, Up, Down, Down, Left, Right, Left, Right) intercepted in `dispatchKeyEvent`. `dispatchKeyEvent` is used instead of a Compose key modifier because Compose key modifiers are bypassed while the `AndroidView`-hosted WebView owns focus. Only the completing key is consumed (which calls `remoteCommandBus.emitOpenDrawer()`); intermediate keys pass through to the dashboard. The combo resets if the user pauses more than `UNLOCK_SEQUENCE_TIMEOUT_MS` (3s) between keys. On mobile `dispatchKeyEvent` is a pure pass-through (guarded by `if (!appConfig.isTv) return super...`).
*   **Leanback Manifest Overlay**: The `src/tv/AndroidManifest.xml` overlay (merged only into `tv*` variants) adds the leanback launcher `intent-filter` (`android.intent.category.LEANBACK_LAUNCHER`) so the app appears on the Android TV home screen, plus `leanback` / `touchscreen` / `camera` / `microphone` `uses-feature` entries marked `required="false"` so the same APK stays installable on non-TV devices.

### TV Injected Dependencies
*   **`AppConfig`** (`presentation-core-platform`): Provides `isTv` for form-factor resolution and key-event gating.
*   **`RemoteCommandBus`** (`presentation-core-platform`): Receives `emitOpenDrawer()` when the D-pad unlock sequence completes.

## Volume-Button Gesture

Unlike the D-pad unlock sequence, which is TV-only, this gesture opens the control drawer on **both mobile and TV** via the hardware volume keys — an alternative entry point for panels where the on-screen FAB is impractical to reach.

*   **Mechanism**: `dispatchKeyEvent` runs `advanceVolumeGesture(keyCode, eventTime)` for every fresh press (`ACTION_DOWN`, `repeatCount == 0`) of `KEYCODE_VOLUME_UP`/`KEYCODE_VOLUME_DOWN`, gated on `InteractionModel.isVolumeGestureOn` (off by default). Presses accumulate `volumePressCount`; if `eventTime - lastVolumePressTime` exceeds `VOLUME_GESTURE_WINDOW_MS` (2s), the count resets — the presses must form a deliberate burst, not ordinary volume adjustment spread over time.
*   **Non-destructive by design**: Only the press that completes `volumeGestureThreshold` is consumed (returns `true`, calls `remoteCommandBus.emitOpenDrawer()`, resets the count); every earlier press returns `false` and is **not** consumed, so the hardware keys keep adjusting device volume normally throughout the burst.
*   **Live settings, no hot-path I/O**: `observeVolumeGestureSettings()` (called once from `onCreate`) collects `ObserveInteractionUseCase` for the lifetime of the activity and mirrors `isVolumeGestureOn`/`pressCountOrDefault` into the plain `volumeGestureEnabled`/`volumeGestureThreshold` fields read by `dispatchKeyEvent`, so the key-event path never awaits a DataStore read. These fields are documented as read/written only from `dispatchKeyEvent`/`onCreate` (main thread), so no synchronization is needed.
*   **Feeds the shared inactivity timer**: every fresh key press — independent of whether it advances the volume gesture — also calls `remoteCommandBus.emitInteraction()`. On TV this is the only interaction signal available to reset an inactivity timer, since D-pad key events never pass through Compose's pointer-input pipeline the way touch does.
*   **Backing model**: `InteractionModel` (`domain-core`), edited on the Settings screen's "Interaction" section (`presentation-feature-settings`) via `SettingsIntent.OnSetInteractionIntent`.

## Dependencies
*   **`presentation-core-navigation-impl`**: To host the application's navigation graph.
*   **`presentation-core-styling`**: For global theme application, plus `FormFactor` / `LocalFormFactor` / `LocalWindowSizeClass`.
*   **`presentation-core-platform`**: For `AppConfig` (form-factor) and `RemoteCommandBus` (TV drawer command and shared interaction events).
*   **`domain-usecase-api`**: For checking onboarding status, observing theme settings, and observing the volume-gesture configuration (`ObserveInteractionUseCase`).
