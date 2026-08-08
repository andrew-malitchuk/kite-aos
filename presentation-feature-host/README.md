# presentation-feature-host

> Entry point and shell for the entire Kite AOS application. Hosts the single `HostActivity`, enforces kiosk-mode system UI, and ensures the app restarts automatically on device boot.

## Responsibility

This module sits at the top of the presentation layer. It is the only module that declares an Android `Activity` and a `BroadcastReceiver` in the manifest. All other feature modules are pure Compose destinations — they know nothing about the Activity lifecycle.

## Architecture

```
HostActivity  ──►  NavGraph (presentation-core-navigation-impl)
     │
     ├── Splash screen (SplashScreen API + custom exit animation)
     ├── Initial route selection (onboarding vs. main)
     ├── Immersive mode enforcement
     ├── Back-gesture blocking (kiosk lockdown)
     ├── Theme observation
     └── Form-factor resolution (mobile / TV) + D-pad drawer unlock
```

### Key Components

| Component | Package | Purpose |
|---|---|---|
| `HostActivity` | `source.host` | Single activity; bootstraps Compose, navigation, and system UI |
| `BootReceiver` | `source.receiver` | `ACTION_BOOT_COMPLETED` listener — relaunches `HostActivity` after device reboot |

## Dependencies

| Module | Purpose |
|---|---|
| `presentation-core-navigation-impl` | Hosts the application navigation graph |
| `presentation-core-styling` | Applies the global Material 3 theme; provides `FormFactor`, `LocalFormFactor`, `LocalWindowSizeClass` |
| `presentation-core-platform` | Provides `AppConfig` (form-factor) and `RemoteCommandBus` (TV drawer command) |
| `domain-usecase-api` | Reads onboarding completion status and observes theme preference |

## Kiosk Lockdown

`HostActivity` enforces two hard constraints on every resume:

1. **Immersive mode** — hides status bar and navigation bar using `WindowInsetsController`. Re-applied in `onWindowFocusChanged` to survive focus loss from dialogs or notifications.
2. **Back-gesture blocking** — a `OnBackPressedCallback` with `isEnabled = true` consumes every back event and never calls through, preventing users from leaving the kiosk screen.

## Android TV

On Android TV the same `HostActivity` acts as the shell. TV concerns are resolved once at the host root so downstream feature modules stay form-factor agnostic.

### Form-Factor Resolution

At the top of `setContent`, `HostActivity` resolves the form factor and window size class and publishes both above `AppTheme`:

```
formFactor      = if (appConfig.isTv) FormFactor.TV else FormFactor.MOBILE
windowSizeClass = calculateWindowSizeClass(activity)

CompositionLocalProvider(
    LocalFormFactor provides formFactor,
    LocalWindowSizeClass provides windowSizeClass,
) { AppTheme { ... } }
```

This lets 10-foot scaling and TV input branches read the form factor without prop-drilling. On mobile both default to `MOBILE`, so behaviour is unchanged.

### D-pad "Settings Unlock"

There is no FAB on TV, so the control drawer is opened by a hidden Konami-style D-pad sequence — **Up, Up, Down, Down, Left, Right, Left, Right** (`SETTINGS_UNLOCK_SEQUENCE`) — intercepted in `dispatchKeyEvent`:

- `dispatchKeyEvent` is used rather than a Compose key modifier because Compose key modifiers are bypassed while the `AndroidView`-hosted WebView owns focus.
- Only the key that **completes** the sequence is consumed; it calls `remoteCommandBus.emitOpenDrawer()`. Intermediate keys pass through so the dashboard still reacts to them.
- The combo resets if the user pauses more than `UNLOCK_SEQUENCE_TIMEOUT_MS` (3s) between keys, so a stale prefix never lingers.
- On mobile `dispatchKeyEvent` is a pure pass-through, guarded by `if (!appConfig.isTv) return super...`, leaving touch behaviour untouched.

### Leanback Manifest Overlay

`src/tv/AndroidManifest.xml` is merged only into `tv*` variants:

- Adds a separate `intent-filter` carrying `android.intent.category.LEANBACK_LAUNCHER` (merged by `android:name` onto the launcher activity) so the app appears on the Android TV home screen. The touch `LAUNCHER` filter stays in the main manifest; neither is duplicated.
- Declares `leanback`, `touchscreen`, `camera` / `camera.external`, and `microphone` as `uses-feature` with `required="false"` so the same APK stays installable on non-TV devices and on TV boxes that lack a touchscreen, camera, or mic.

### TV Injected Dependencies

| Dependency | Module | Purpose |
|---|---|---|
| `AppConfig` | `presentation-core-platform` | Exposes `isTv` for form-factor resolution and key-event gating |
| `RemoteCommandBus` | `presentation-core-platform` | Receives `emitOpenDrawer()` when the D-pad unlock sequence completes |

## Splash Screen

The splash screen uses the Android 12+ `SplashScreen` API (`androidx.core.splashscreen`). Dismissal is deferred until the `HostViewModel` signals that the initial data load (onboarding status check) is complete. A custom exit animation fades and scales the splash icon out before the first Compose frame is drawn.

## Boot Persistence

`BootReceiver` is registered for `android.intent.action.BOOT_COMPLETED`. On receipt it starts `HostActivity` with `FLAG_ACTIVITY_NEW_TASK`. The `RECEIVE_BOOT_COMPLETED` permission is declared in the manifest. No additional configuration is needed — the receiver fires automatically after any reboot.

## Initial Route Selection

On cold start, `HostActivity` asks `ObserveOnboardingStatusUseCase` whether the user has completed the onboarding wizard:

- **Not completed** → navigate to the `Onboarding` destination.
- **Completed** → navigate to the `Main` destination.

This decision happens once per process launch; subsequent navigation is handled entirely by the navigation graph.

## Theme Observation

`HostActivity` collects `ObserveThemeUseCase` as a `StateFlow` and delegates to `AppCompatDelegate.setDefaultNightMode()` so the entire Compose hierarchy picks up the correct dark/light mode before the first frame renders.
