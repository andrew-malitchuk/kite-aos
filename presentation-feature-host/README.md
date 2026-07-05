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
     └── Theme observation
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
| `presentation-core-styling` | Applies the global Material 3 theme |
| `domain-usecase-api` | Reads onboarding completion status and observes theme preference |

## Kiosk Lockdown

`HostActivity` enforces two hard constraints on every resume:

1. **Immersive mode** — hides status bar and navigation bar using `WindowInsetsController`. Re-applied in `onWindowFocusChanged` to survive focus loss from dialogs or notifications.
2. **Back-gesture blocking** — a `OnBackPressedCallback` with `isEnabled = true` consumes every back event and never calls through, preventing users from leaving the kiosk screen.

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
