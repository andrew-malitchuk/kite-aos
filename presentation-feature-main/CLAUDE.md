# Module: presentation-feature-main

## Overview
This feature module contains the main screen of the application, serving as the primary kiosk interface. It is responsible for hosting the central dashboard (web-based) and providing seamless navigation to other allowed applications and system settings.

## Responsibilities
*   **Web Kiosk Hosting**: Manages a full-screen `WebView` optimized for long-running dashboard displays (JS enabled, DOM storage, hardware acceleration).
*   **Navigation & Application Hub**: Provides a hidden "Control Drawer" that allows users to switch between allowed system applications, reload the kiosk, or jump to settings.
*   **Motion-Activated UI**: Automatically manages the visibility of the "Open Drawer" FAB (Floating Action Button) based on motion detection events from the `MotionService`. On Android TV the FAB is suppressed and the drawer is opened via a hidden remote key combo instead (see [Android TV](#android-tv)).
*   **Domain Security**: Implements a URL whitelist to ensure the kiosk remains locked to authorized domains and prevents unauthorized browsing.
*   **Immersive Experience**: Enforces a full-screen interface by hiding system bars and managing activity-level immersive settings.

## Architecture
The module follows the **MVI (Model-View-Intent)** pattern using the **Orbit** framework:
*   **`MainState`**: Holds the current kiosk configuration (URL, whitelist, dock position) and transient UI state (FAB visibility, loading status).
*   **`MainIntent`**: Dispatched by the UI to trigger loads, reloads, or navigation to settings/apps.
*   **`MainSideEffect`**: Handles one-off events such as opening external applications, displaying errors via the snackbar system, or opening the control drawer (`OpenDrawerEffect`, fired on Android TV).
*   **`MainViewModel`**: The core coordinator. It orchestrates initial data loading and subscribes to the motion detection flow to toggle the navigation UI. It also observes `RemoteCommandBus.openDrawer` and the `ScreenStateModel` stream (which drives both the screensaver and the TV dark overlay).

## Key Components
*   **`KioskWebView`**: A specialized `WebView` wrapper that handles state restoration, custom loading indicators (shimmer), and navigation filtering.
*   **`ControlDrawer`**: A dual-layout component that adapts to the configured dock position (Bottom or Left), providing navigation controls and app shortcuts.
*   **`SideBar`**: A glassmorphic (blurred) overlay implementation using the **Haze** library, ensuring the drawer feels like a modern layer over the dashboard.
*   **Draggable FAB**: A motion-aware action button that users can reposition to avoid obscuring dashboard elements.

## Android TV
*(`@since 1.2.0`)*

On the `tv` form factor the kiosk is driven by a remote D-pad rather than touch. The screen adapts its behaviour based on `LocalFormFactor.current == FormFactor.TV`. See the project-wide [Android TV support](../../docs/android-tv.md) reference for the full picture.

*   **Dark overlay (screen-off stand-in)**: Android TV cannot power its panel off or lock the device, so instead of a device lock the `DarkOverlay()` composable (in `source/screensaver/`) draws a plain full-screen black overlay. It is driven by the `isDarkOverlayVisible` flag in `MainState`, which the ViewModel toggles when `ScreenStateModel.DarkOverlay` is observed. Unlike `ScreensaverOverlay` it never shows images or a clock — its sole job is to stop the screen glowing. It dismisses when motion (or a remote key press) flips the screen state back to `Active`.
*   **Remote-driven drawer**: There is no FAB on TV (`state.isFabVisible && !isTv`). Instead, `HostActivity` intercepts a hidden long-press D-pad combo and emits it on `RemoteCommandBus.openDrawer`. `MainViewModel` collects that flow and posts `MainSideEffect.OpenDrawerEffect`, which `MainScreen` turns into an `openDrawerTrigger` that raises the drawer in `MainContent`.
*   **Drawer initial focus**: `ControlDrawer` takes a `requestInitialFocus` parameter (`isTv && isOpened`). When set, it lands D-pad focus on the first (Settings) button after a short delay (once the drawer has composed/animated in) so the remote has a starting point.
*   **BACK closes the drawer**: `MainContent` installs `BackHandler(enabled = isTv && isOpened)` so the remote BACK key closes an open drawer (there is no scrim to tap on TV). It is enabled only while the drawer is open so it takes priority over the global kiosk back-blocker; mobile is unchanged.
*   **WebView D-pad focus**: both `AndroidWebViewEngine` and `GeckoViewEngine` request focus on the underlying view once it is attached (in their `AndroidView` `update` block) so remote key events reach the Home Assistant dashboard. In v1 focus quality inside the page relies on HA's own focus handling — there is no custom in-page JS focus manager.
*   **Leanback launch fallback**: when launching an external app, `MainScreen` falls back to `PackageManager.getLeanbackLaunchIntentForPackage()` if the regular `getLaunchIntentForPackage()` returns `null` (common for TV-only apps that have no `CATEGORY_LAUNCHER` entry).

## Survivability & Interaction (`@since 2.2.0`)
The Main screen owns the mounted engine, so every behaviour that has to touch it is routed here —
even when the trigger lives outside the composition (an `AlarmManager` alarm, an
`Application.onTrimMemory` callback, an MQTT button). Those arrive on `RemoteCommandBus` and are
turned into side effects by `MainViewModel`.

*   **`EngineHandle` gained `pause()` / `resume()` / `clearCache()`**, implemented by both engines.
    `pause()` is what stops the Home Assistant frontend's ~1/second WebSocket retry loop while the
    backend is down. On GeckoView that is `session.setActive(false)`; on Android WebView it needs
    **both** `onPause()` and `pauseTimers()` — `onPause()` alone only stops drawing, leaving the JS
    reconnect loop running.
*   **`clearCache()` preserves the session.** Only HTTP and image caches are dropped
    (`ClearFlags.ALL_CACHES` / `clearCache(true)`). Clearing cookies or local storage would sign the
    panel out of Home Assistant, turning a remote recovery into a physical visit.
*   **`ReloadWebViewEffect` vs `NavigateHomeEffect`.** A reload re-requests the current page, so the
    dashboard's path survives — that is what the scheduled and memory-pressure reloads want. The
    inactivity reset wants the opposite, so it assigns the home URL directly.
*   **Interaction detection.** The idle countdown is reset by a `pointerInput` attached to
    `KioskWebView` that observes the `PointerEventPass.Initial` pass and **never consumes** — a
    normal gesture detector there would swallow the touches the dashboard needs. TV has no touch, so
    D-pad presses arrive instead via `RemoteCommandBus.interaction` from `HostActivity`.
*   **Remote commands run through one channel.** `MainScreen` no longer keeps a counter per
    engine action; every instruction is dispatched as a single `EngineCommand(id, action, value)`
    that `MainContent` applies in one `LaunchedEffect`. The `id` matters: these are events, not
    state, so two reloads or two navigations to the same URL must act twice — an unchanged key
    would swallow the second. `EngineHandle` gained `evaluateJs(script)` for the MQTT `evaluate_js`
    command; it is honoured on Android WebView and **ignored (with a log) on GeckoView**, which
    blocks `javascript:` loads and evaluates only through a signed WebExtension.
*   **`navigate` bypasses the whitelist by design.** The whitelist stops a passer-by wandering off
    the dashboard, and a publish to the broker is not a passer-by. It works out that way in
    practice too: assigning `state.url` reaches the engine through `loadUrl()`, which does not run
    `shouldOverrideUrlLoading`.
*   The countdown does not run while the screensaver or dark overlay is up: an idle panel is already
    showing nothing, and resetting behind the screensaver would silently discard the user's page.

## Dependencies
*   **`presentation-core-ui`**: For design system atoms, molecules (like `SimpleApplicationListItem`), and shimmer effects.
*   **`presentation-core-platform`**: For direct integration with the `MotionService`.
*   **`domain-usecase-api`**: For accessing configuration settings and hardware sensor observations.
*   **Haze**: For modern GPU-accelerated blur effects.
