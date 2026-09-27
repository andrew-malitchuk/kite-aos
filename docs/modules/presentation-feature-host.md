# presentation-feature-host

The entry point and host module for the "kite-aos" application.

## Features
- **Single Activity Architecture**: Hosts the entire application within a single `HostActivity`.
- **Advanced Splash Screen**: Custom splash screen logic with smooth transitions and state-based dismissal.
- **Kiosk Mode Enforcement**: Immersive full-screen mode and global back-gesture blocking.
- **Boot Persistence**: Automatically launches on device boot via `BootReceiver`.
- **Android TV Support**: Resolves the form factor at the host root and opens the control drawer via a hidden D-pad sequence on TV.
- **Volume-Button Gesture** (`@since 2.2.0`): Opens the control drawer from a burst of hardware volume presses, on both mobile and TV.

## Volume-Button Gesture (`@since 2.2.0`)

Unlike the D-pad unlock sequence below, this gesture works on **both mobile and TV** — for panels where the on-screen FAB is impractical to reach (behind glass, wall-mounted, or simply out of the user's habit).

- `dispatchKeyEvent` feeds every fresh press (`ACTION_DOWN`, `repeatCount == 0`) of `KEYCODE_VOLUME_UP` or `KEYCODE_VOLUME_DOWN` into `advanceVolumeGesture()`, gated by `InteractionModel.isVolumeGestureOn` (off by default).
- Presses must land within a rolling `VOLUME_GESTURE_WINDOW_MS` (2s) window of the previous one, or the count resets — a deliberate burst is required, not volume adjustment spread out over time.
- Only the press that **completes** the configured count is consumed (`remoteCommandBus.emitOpenDrawer()`, count reset to 0); every earlier press returns `false` so the hardware keys keep adjusting volume normally.
- The required count comes from `InteractionModel.pressCountOrDefault` (edited in the Settings screen's "Interaction" section), kept live via `observeVolumeGestureSettings()` collecting `ObserveInteractionUseCase` — this avoids a DataStore read on the hot key-event path.
- Every fresh key press, whether or not it advances the gesture, also calls `remoteCommandBus.emitInteraction()`. On TV this is the only interaction signal available for the shared inactivity-reset timer, since D-pad input never reaches Compose's pointer-input pipeline; on mobile, touch events cover that separately.

## Android TV
On Android TV the same `HostActivity` acts as the shell; TV concerns are resolved at the host root so feature modules stay form-factor agnostic.

- **Form-Factor Resolution**: Computes `formFactor = if (appConfig.isTv) FormFactor.TV else FormFactor.MOBILE` and `calculateWindowSizeClass(activity)`, publishing both above `AppTheme` via `CompositionLocalProvider(LocalFormFactor provides ..., LocalWindowSizeClass provides ...)`. This drives 10-foot scaling and TV input branches without prop-drilling. On mobile both default to `MOBILE`, so behaviour is unchanged.
- **D-pad "Settings Unlock"**: There is no FAB on TV, so the control drawer is opened by a hidden Konami-style D-pad sequence (Up, Up, Down, Down, Left, Right, Left, Right) intercepted in `dispatchKeyEvent`. `dispatchKeyEvent` is used instead of a Compose key modifier because the latter is bypassed while the `AndroidView`-hosted WebView owns focus. Only the completing key is consumed (calling `remoteCommandBus.emitOpenDrawer()`); intermediate keys pass through to the dashboard, and the combo resets after a >3s pause (`UNLOCK_SEQUENCE_TIMEOUT_MS`). On mobile it is a pure pass-through, guarded by `if (!appConfig.isTv) return super...`.
- **Leanback Manifest Overlay**: `src/tv/AndroidManifest.xml` (merged only into `tv*` variants) adds the `android.intent.category.LEANBACK_LAUNCHER` intent-filter so the app appears on the Android TV home screen, plus `leanback` / `touchscreen` / `camera` / `microphone` `uses-feature` entries marked `required="false"` so the same APK stays installable on non-TV devices.
- **TV Dependencies**: Injects `AppConfig` (for `isTv`) and `RemoteCommandBus` (for `emitOpenDrawer()`), both from `presentation-core-platform`.

## Usage
This module is the application's entry point. The `HostActivity` is declared as the launcher activity in the `AndroidManifest.xml`. No manual instantiation of components from this module is required in other feature modules.
