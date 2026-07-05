# presentation-feature-settings

The configuration center for the Kite kiosk application. All user-facing settings are exposed through a single scrollable screen grouped into logical sections.

## Settings Sections

| Section | What it controls |
|---|---|
| **Mode detector** | Camera-based presence detection: sensitivity, dim delay, screen timeout, FAB appearance delay. |
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
- **Camera Streaming**: Configure MJPEG server port, quality, FPS, and frame rotation.
- **Screensaver**: Configure idle overlay with image slideshow, clock display, and activation timing.
- **Auto Reboot**: Set a scheduled daily/weekly/bi-weekly/monthly reboot at a specific time.
- **MQTT Setup**: Configure broker connection details for remote telemetry and Home Assistant discovery.
- **WebView Refresh**: Enable periodic automatic reloads of the kiosk dashboard.
- **Dock Positioning**: Choose the control drawer anchor (left or top edge).
- **Localization**: Change the application language dynamically (English / Ukrainian).
- **Reduce Motion**: Disable all UI animations globally to improve performance on slower devices.
- **Config Import / Export**: Backup and restore all settings as a JSON file via the system file picker.

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
