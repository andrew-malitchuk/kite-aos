# Module: presentation-feature-settings

## Overview
This feature module provides the "Settings" screen for the application. It allows users to configure global settings such as the UI theme, dock position, kiosk dashboard URLs, motion detector parameters, and MQTT telemetry configuration.

## Responsibilities
*   **User Interface**: Renders a comprehensive settings interface using Jetpack Compose and the project's shared UI kit.
*   **State Management**: Implements the MVI (Model-View-Intent) pattern using the Orbit framework to manage settings state and asynchronous updates.
*   **Domain Integration**: Orchestrates domain logic by calling multiple use cases to fetch and persist user preferences.
*   **Localization**: Supports dynamic language switching, including deep-linking to system language settings on modern Android versions.
*   **Navigation**: Handles navigation events to external screens like "About" and "Application Selection".

## Architecture
This module follows the standard feature architecture of the project:
*   **MVI (Orbit)**:
    *   **`SettingsState`**: Represents the full configuration state (Theme, Dock, MQTT, etc.).
    *   **`SettingsIntent`**: Encapsulates user actions (e.g., `OnSetThemeIntent`, `OnSetDashboardIntent`).
    *   **`SettingsSideEffect`**: Manages one-off events like navigation and error reporting.
*   **ViewModel**: `SettingsViewModel` acts as the container host, debouncing high-frequency updates (like text input) before persisting them via use cases.
*   **Composables**:
    *   `SettingsScreen`: The entry point that integrates with the ViewModel and handles side effects.
    *   `SettingsContent`: The main UI implementation featuring smooth entry animations and a structured list layout.

## Key Components
*   **`SettingsViewModel`**: The primary logic controller, interacting with over 10 different domain use cases.
*   **`SettingsContent`**: A complex layout utilizing specialized UI molecules like `NumberInputListItem` and `ThemeListItem`.
*   **`LanguageSelectionDialog`**: An internal component for in-app language switching when system settings are unavailable.
*   **Camera-source selector** (`@since 1.4.0`): A tappable `SimpleListItem` inside `MoveDetectorSection` that cycles the camera feeding motion detection and MJPEG streaming through `CameraSourceModel` — `Auto`, `Front`, `Rear`, `External` (USB). Backed by `SettingsViewModel.onSetCameraSource` (via `GetCameraSourceUseCase` / `SetCameraSourceUseCase`), which persists the choice optimistically; `MotionService` re-selects the frame source on the next emission. Changes are dispatched through `SettingsIntent.OnSetCameraSourceIntent`. The selector works on all form factors — including USB-OTG webcams on phones/tablets — not only TV. On TV the cycle collapses to `Auto` ⇄ `External` since leanback boxes have no built-in front/rear camera; any unavailable choice still falls back gracefully at runtime in `MotionSourceFactory`.

## Android TV
On TV, `SettingsContent` branches on `LocalFormFactor.current == FormFactor.TV` and renders a dedicated `SettingsTvContent` composable instead of the scrollable mobile column. This is the only layout that differs; all section composables (`MoveDetectorSection`, `MqttSection`, `WebKioskSection`, etc.) are reused verbatim, so auto-save, validation, and debouncing behave identically on both form factors.

*   **Master/detail layout**: A two-pane `Row` split by weight constants `SETTINGS_TV_MASTER_WEIGHT` (0.38) and `SETTINGS_TV_DETAIL_WEIGHT` (0.62). The left (master) pane lists the sections; the right (detail) pane renders the currently selected section in full. Sections are enumerated by the private `SettingsSectionTab` enum (title + icon per entry), which maps one-to-one to the mobile section composables via the `when (selectedTab)` block in the detail pane.
*   **Single-focus-target list (D-pad)**: The entire master `Column` is *one* focus target (`.focusRequester(listFocusRequester).onPreviewKeyEvent { … }.focusable()`); the individual `BaseListItem` rows are **non-focusable** and carry no `onClick`. Navigation is driven explicitly through `onPreviewKeyEvent` (handling `KeyEventType.KeyDown` only) rather than per-row focusable items — Compose's default spatial focus search would otherwise hijack the DPAD event and throw focus onto the detail pane. Handler behaviour: `DirectionDown`/`DirectionUp` mutate the `selectedTab` state (not focus) and consume the event; `DirectionCenter`/`Enter`/`NumPadEnter`/`DirectionRight` call `detailFocusRequester.requestFocus()` to commit into the detail pane; `DirectionLeft` is swallowed; `DirectionUp` at index 0 returns `false` so the header action buttons stay reachable.
*   **Highlight + autoscroll**: The selected row is highlighted via the `selectedTab == tab` check (brand-colored icon background). Because the rows are non-focusable, focus-driven scroll is unavailable, so each row registers a `BringIntoViewRequester` (`rowBringIntoView` map) and a `LaunchedEffect(selectedTab)` scrolls the highlighted row into view. Nothing in the detail subtree is focusable while the user only browses the list, so no control can steal focus before the user commits with OK/RIGHT.
*   **Returning to the list**: The detail pane declares `Modifier.focusProperties { left = listFocusRequester }`, so pressing LEFT inside the detail pane hands focus back to the section list.
*   **Overscan + initial focus**: On TV the whole screen is inset by `Theme.spacing.sizeL` (`overscan`) to keep controls out of the TV bezel dead zone; on mobile the inset is `0.dp`. A `LaunchedEffect(Unit)` seeds initial D-pad focus onto the section list via `listFocusRequester` so the first remote press edits rather than merely pulling focus into the screen.
*   **D-pad hint legend**: `TvActionHints` renders a compact `↕ / OK / ←` legend (`TvHintChip`) at the bottom of the detail pane. There is no explicit Save button — settings auto-save, matching the mobile behaviour. Settings on TV is opened via a hidden D-pad combo in the host activity (there is no FAB and no PIN gate); that entry point lives outside this module.

## Dependencies
*   **`domain-usecase-api`**: For all business logic operations.
*   **`presentation-core-ui`**: For consistent design system components.
*   **`presentation-core-navigation-api`**: For navigating between features.
*   **Orbit MVI**: Core state management library.
