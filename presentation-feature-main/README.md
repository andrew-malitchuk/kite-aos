# presentation-feature-main

This module implements the primary full-screen kiosk experience. It is the heart of the "Kite" application.

## Features

*   **Fullscreen Dashboard**: Optimized for smart home dashboards like Home Assistant.
*   **Motion-Sensed Controls**: The "Open Drawer" button only appears when motion is detected, ensuring a clean, distraction-free display. (On Android TV the button is replaced by a hidden remote key combo — see below.)
*   **App Hub**: A quick-access drawer to launch other allowed applications without exiting the kiosk mode.
*   **Glassmorphism**: Modern, blurred UI layers that adapt to your theme.
*   **Flexible Docking**: Supports positioning the control drawer at either the bottom or the left side of the screen.

## Technical Highlights

### Motion & UI Sync
The module leverages the `MotionService` to detect user presence. The `MainViewModel` manages a visibility timer:
1.  On motion detected → Show FAB.
2.  Start/Reset a countdown timer (e.g., 60 seconds).
3.  On timer expire → Fade out FAB.

### Web Security
The `KioskWebView` is strictly bounded by a whitelist. If an external link is clicked that doesn't match the configured whitelist domain, the navigation is blocked, keeping the tablet secured to the intended dashboard.

## Android TV
*(`@since 1.2.0`)*

The kiosk runs on the `tv` form factor as a remote-driven, 10-foot experience. Behaviour adapts when `LocalFormFactor` is `TV`; see the project [Android TV support](../docs/android-tv.md) reference for the full picture.

*   **Dark overlay instead of screen-off**: Android TV can't power its panel off or lock the device, so a plain full-screen black overlay (`DarkOverlay`) stands in for the screen-off state. Unlike the screensaver it shows no images or clock — it just stops the screen glowing, and dismisses on motion or a remote key press.
*   **Remote key combo opens the drawer**: with no touchscreen there is no FAB. A hidden long-press D-pad combo (handled in `HostActivity`) opens the control drawer via the `RemoteCommandBus`.
*   **D-pad-ready drawer**: when the drawer opens on TV it lands focus on the first (Settings) button so the remote has a starting point, and the BACK key closes it (there is no scrim to tap).
*   **WebView remote focus**: both the Android system WebView and GeckoView engines take D-pad focus on entry so remote keys reach the Home Assistant dashboard (in-page focus relies on HA's own handling).
*   **Leanback app launch**: launching TV-only apps falls back to the leanback launch intent when the regular launcher intent is unavailable.

## Internal Structure

*   `source/main/`: Primary MVI orchestration (Screen, ViewModel, State).
*   `source/webview/`: Custom secured WebView implementation.
*   `source/drawer/`: Control drawer UI and app-switching logic.
*   `core/components/`: Reusable layout components like the `SideBar`.
