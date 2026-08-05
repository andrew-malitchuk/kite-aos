# presentation-feature-settings

The configuration center for the Home Kiosk application.

## Features
- **Theme Selection**: Switch between Light, Dark, and Material You (System) themes.
- **Kiosk Configuration**: Set the dashboard URL and whitelist for the web view.
- **Motion Detector**: Fine-tune sensitivity, dim delays, and screen timeouts.
- **Camera Source** (`@since 1.4.0`): Pick which camera feeds motion detection and MJPEG streaming — Auto / Front / Rear / External (USB). Tap to cycle; works on all form factors, including USB-OTG webcams on phones.
- **MQTT Setup**: Configure broker connection details for remote telemetry and control.
- **Dock Positioning**: Choose the location of the control drawer (Left or Top).
- **Localization**: Change the application language dynamically.
- **Android TV**: A D-pad-driven master/detail layout for leanback devices (see below).

## Android TV
On TV, `SettingsContent` branches on `LocalFormFactor.TV` and renders `SettingsTvContent` — a master/detail layout instead of the mobile scrollable column. The section composables are reused unchanged.
- **Master/detail**: A section list (master) beside a detail pane that shows the selected section in full.
- **Single focus target**: The whole list is one focusable target with non-focusable rows; D-pad navigation is handled via `Modifier.onPreviewKeyEvent` (not per-row focus or directional `focusProperties`), so Compose's spatial focus search can't hijack the DPAD event onto the detail pane.
- **Controls**: Up/Down move the highlighted section (state, not focus); OK/Right enter the detail pane; Left returns to the list. The selected row auto-scrolls into view via `BringIntoViewRequester`.
- **Details**: The screen is inset for TV overscan, initial focus is seeded onto the list, and a `↕ / OK / ←` hint legend replaces the FAB. Settings auto-save (no Save button); opened via a hidden host-activity D-pad combo with no PIN gate.

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
