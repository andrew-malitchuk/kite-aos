# presentation-feature-settings

The configuration center for the Kite kiosk application. All user-facing settings are exposed through a single scrollable screen grouped into logical sections.

## Settings Sections

| Section | What it controls |
|---|---|
| **Mode detector** | Camera-based presence detection: camera source, sensitivity, dim delay, screen timeout, FAB appearance delay. |
| **Camera streaming** | MJPEG stream server: port, quality, FPS, and output rotation. |
| **Screensaver** | Idle overlay: activation delay, slideshow interval, clock overlay, image folder. |
| **Auto Reboot** | Scheduled device reboots: hour, minute, and interval (daily / weekly / bi-weekly / monthly). |
| **MQTT** | Broker connection: IP address, port, client ID, username, password, friendly name. |
| **Web kiosk** | Dashboard URL, URL whitelist, browser engine (WebView / GeckoView), allowed applications, auto-return, SSL trust. |
| **WebView refresh** | Periodic automatic page reload: toggle and interval in seconds. |
| **UI & UX** | Theme (light / dark / system), dock position (top / left), language, reduce motion. |
| **System** | Set as default launcher, restart application. |
| **Advanced** | Export and import full configuration as JSON. |

## Features

- **Theme Selection**: Switch between Light, Dark, and Material You (System) themes with a circular-reveal transition animation.
- **Kiosk Configuration**: Set the dashboard URL and whitelist for the web view. Toggle auto-return to kiosk and SSL certificate trust.
- **Browser Engine**: Toggle between Android WebView and GeckoView (Firefox) inline.
- **Motion Detector**: Fine-tune sensitivity, dim delay, screen timeout, and FAB appearance delay.
- **Camera Source** (`@since 1.4.0`): Choose which camera drives motion detection and MJPEG streaming — Auto / Front / Rear / External (USB). A tappable `SimpleListItem` in the Mode-detector section cycles the choices, backed by `CameraSourceModel` and `Get`/`SetCameraSourceUseCase` in `SettingsViewModel`; the change is applied optimistically and re-picked up by `MotionService`. Available on every form factor (USB-OTG webcams on phones/tablets included), not just TV — on TV the cycle collapses to Auto ⇄ External since leanback boxes lack a built-in front/rear camera.
- **Camera Streaming**: Configure MJPEG server port, quality, FPS, and frame rotation.
- **Screensaver**: Configure idle overlay with image slideshow, clock display, and activation timing.
- **Auto Reboot**: Set a scheduled daily/weekly/bi-weekly/monthly reboot at a specific time.
- **MQTT Setup**: Configure broker connection details for remote telemetry and Home Assistant discovery.
- **WebView Refresh**: Enable periodic automatic reloads of the kiosk dashboard.
- **Dock Positioning**: Choose the control drawer anchor (left or top edge).
- **Localization**: Change the application language dynamically (English / Ukrainian).
- **Reduce Motion**: Disable all UI animations globally to improve performance on slower devices.
- **Config Import / Export**: Backup and restore all settings as a JSON file via the system file picker.

## Android TV

On Android TV the same settings screen is rendered as a **master/detail layout** driven by a D-pad remote instead of the mobile scrollable column. `SettingsContent` detects the form factor (`LocalFormFactor.current == FormFactor.TV`) and delegates to a dedicated `SettingsTvContent`. Every section composable is reused unchanged, so auto-save, validation, and debouncing behave identically on both form factors.

- **Two panes**: A narrow section list (master, ~38% width) sits beside a wider detail pane (~62% width) that renders the selected section in full. Sections come from the `SettingsSectionTab` enum, which maps one-to-one to the existing mobile section composables.
- **Single focus target**: The whole section list is a *single* focusable target; the rows themselves are non-focusable visuals. Navigation is handled explicitly through `Modifier.onPreviewKeyEvent` rather than per-row focusable items or `focusProperties` directional redirects for up/down — Compose's spatial focus search would otherwise hijack the DPAD event and throw focus straight onto the detail pane.
- **D-pad controls** (KeyDown only): Up/Down move the highlighted section by mutating a `selectedTab` state (not focus) and consume the event; OK / Center / Right commit into the detail pane by requesting focus there; Left is swallowed; Up on the first row is passed through so the header actions stay reachable. Inside the detail pane, Left returns focus to the section list (`focusProperties { left = listFocusRequester }`).
- **Highlight & autoscroll**: The selected row is highlighted and scrolled into view via a `BringIntoViewRequester` plus a `LaunchedEffect(selectedTab)`, since focus-driven scrolling is unavailable once rows are non-focusable. No control in the detail pane is focusable while the user is merely browsing, so nothing steals focus before the user commits with OK.
- **Overscan & focus seeding**: The whole screen is inset (`Theme.spacing.sizeL`) to avoid the TV overscan dead zone, and initial D-pad focus is seeded onto the section list so the first remote press edits immediately.
- **Affordances**: A compact `↕ / OK / ←` hint legend replaces the mobile FAB. Settings auto-save (no Save button). On TV the screen is opened via a hidden D-pad combo in the host activity — there is no FAB and no PIN gate (home use).

## Usage

This module is navigated to via the `Destination.Settings` route. It provides its own navigation handling and ViewModel.

## Technical Details

- **Architecture**: MVI with Orbit.
- **Animations**: Uses `CircularReveal` for theme transitions and `AnimationSequenceHost` for entry animations. Disabled globally when *Reduce Motion* is on.
- **Debouncing**: Changes to text-based settings (URLs, MQTT fields) are automatically debounced in the ViewModel to prevent excessive DataStore writes.
- **Persistence**: All settings are stored in Proto DataStore. Changes take effect immediately without a restart unless noted.
- **Version footer**: The version string (`vX.Y.Z (build)`) is displayed at the bottom of the screen.

## Internal Structure

- `di/`: Koin module definition (`PresentationFeatureSettingsModule`).
- `source/settings/`:
    - `SettingsScreen.kt`: Navigation entry point and side-effect handling.
    - `SettingsContent.kt`: Stateless UI layout and composable components.
    - `SettingsViewModel.kt`: Business logic orchestration (`ContainerHost<State, SideEffect>`).
    - `SettingsContract.kt`: `State` data class, `SideEffect` sealed class.
    - `SettingsIntent.kt`: Sealed class of user actions dispatched to the ViewModel.
