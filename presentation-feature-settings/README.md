# presentation-feature-settings

The configuration center for the Home Kiosk application.

## Features
- **Theme Selection**: Switch between Light, Dark, and Material You (System) themes.
- **Kiosk Configuration**: Set the dashboard URL and whitelist for the web view.
- **Motion Detector**: Fine-tune sensitivity, dim delays, and screen timeouts.
- **MQTT Setup**: Configure broker connection details for remote telemetry and control.
- **Dock Positioning**: Choose the location of the control drawer (Left or Top).
- **Localization**: Change the application language dynamically.

## Usage
This module is intended to be navigated to via the `Destination.Settings` route. It provides its own navigation handling and ViewModel.

## Technical Details
- **Architecture**: MVI with Orbit.
- **Animations**: Uses `CircularReveal` for theme transitions and `AnimationSequenceHost` for entry animations.
- **Debouncing**: Changes to text-based settings (URLs, MQTT details) are automatically debounced in the ViewModel to prevent excessive storage writes.

## Internal Structure
- `di/`: Koin module definition.
- `source/settings/`:
    - `SettingsScreen.kt`: Navigation and side-effect handling.
    - `SettingsContent.kt`: UI layout and components.
    - `SettingsViewModel.kt`: Business logic orchestration.
    - `SettingsContract.kt`: State, Intent, and SideEffect definitions.
