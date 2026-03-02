# presentation-feature-main

This module implements the primary full-screen kiosk experience. It is the heart of the "Kite" application.

## Features

*   **Fullscreen Dashboard**: Optimized for smart home dashboards like Home Assistant.
*   **Motion-Sensed Controls**: The "Open Drawer" button only appears when motion is detected, ensuring a clean, distraction-free display.
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

## Internal Structure

*   `source/main/`: Primary MVI orchestration (Screen, ViewModel, State).
*   `source/webview/`: Custom secured WebView implementation.
*   `source/drawer/`: Control drawer UI and app-switching logic.
*   `core/components/`: Reusable layout components like the `SideBar`.
