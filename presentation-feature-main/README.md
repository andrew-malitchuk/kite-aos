# presentation-feature-main

This module implements the primary full-screen kiosk experience. It is the heart of the "Kite" application.

## Features

*   **Fullscreen Dashboard**: Optimized for smart home dashboards like Home Assistant.
*   **Motion-Sensed Controls**: The "Open Drawer" button only appears when motion is detected, ensuring a clean, distraction-free display. (On Android TV the button is replaced by a hidden remote key combo — see below.)
*   **App Hub**: A quick-access drawer to launch other allowed applications without exiting the kiosk mode.
*   **Glassmorphism**: Modern, blurred UI layers that adapt to your theme.
*   **Flexible Docking**: Supports positioning the control drawer at either the bottom or the left side of the screen.
*   **Survivability & Interaction** (`@since 2.2.0`): The engine can be paused/resumed and cache-cleared on command, reloads without losing your place, and detects real touches or D-pad presses without stealing them from the dashboard — see [Survivability & Interaction](#survivability--interaction) below.

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

## Survivability & Interaction (`@since 2.2.0`)

The Main screen owns the mounted WebView, so it is where every "keep the panel alive" feature from `presentation-core-platform` actually acts — even when the trigger comes from outside the screen (a scheduled alarm, a memory-pressure callback, an MQTT button, a D-pad press).

*   **Pause / resume**: the connection monitor pauses the engine while the Home Assistant backend is unreachable — stopping its retry loop dead — and resumes it once the backend answers again.
*   **Cache clear keeps you signed in**: the remote "clear cache" command drops only HTTP and image caches, never cookies or local storage, so recovering a stuck dashboard never signs it out of Home Assistant.
*   **Reload vs. navigate home**: a scheduled or memory-pressure reload re-requests the current page, so the dashboard stays where it was; the inactivity timeout instead jumps back to the configured home URL.
*   **Touches and D-pad presses reset the idle timer without being swallowed**: on mobile, a passive touch observer resets the countdown without intercepting the gesture the dashboard needs; on Android TV (no touchscreen) the same reset is driven by remote key presses instead.
*   **One command channel**: MQTT/automation instructions (reload, navigate, back, forward, clear cache, evaluate JS, ...) are dispatched through a single engine command channel, so two identical commands in a row both take effect. Running arbitrary JavaScript is only honored on the Android system WebView engine — it is unsupported (and safely ignored) on GeckoView.
*   **The `navigate` command bypasses the URL whitelist on purpose**: it is meant for trusted Home Assistant automations, not for a passer-by browsing away from the dashboard.
*   The idle countdown pauses while the screensaver or the TV dark overlay is showing, so it never silently discards your page behind them.

## Internal Structure

*   `source/main/`: Primary MVI orchestration (Screen, ViewModel, State).
*   `source/webview/`: Custom secured WebView implementation.
*   `source/drawer/`: Control drawer UI and app-switching logic.
*   `core/components/`: Reusable layout components like the `SideBar`.
