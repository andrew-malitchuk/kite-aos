# Module: presentation-feature-onboarding

## Overview
This feature module manages the initial setup experience for the application. It guides the user through a multi-step "Onboarding" wizard to ensure all necessary permissions are granted and initial configurations (like the dashboard URL) are set before the kiosk mode becomes active.

## Responsibilities
*   **Onboarding Wizard**: Provides a structured, multi-page setup flow using the `WizardPager` component.
*   **Permission Orchestration**: Interacts with the Android system to request and verify critical permissions:
    *   `CAMERA`: Required for the `MotionService`.
    *   `SYSTEM_ALERT_WINDOW` (Overlay): Essential for kiosk mode to stay on top.
    *   `POST_NOTIFICATIONS`: For service status updates.
    *   `DEVICE_ADMIN`: Required for programmatic screen locking.
    *   `WRITE_SETTINGS`: For brightness management.
*   **Initial Configuration**: Collects the primary Dashboard URL and Whitelist patterns from the user.
*   **State Persistence**: Uses domain use cases to save the setup completion status and URL settings.

## Architecture
The module follows the **MVI (Model-View-Intent)** architectural pattern using the **Orbit** framework:
*   **`OnboardingState`**: Tracks permission statuses and collected URL data.
*   **`OnboardingIntent`**: Represents user actions like clicking a permission toggle or finishing the setup.
*   **`OnboardingSideEffect`**: Handles one-off events like launching system permission dialogs or navigating to the main screen.
*   **`OnboardingViewModel`**: Orchestrates the logic, interacting with `GetDashboardUseCase`, `SetDashboardUseCase`, and `SetOnboardingStatusUseCase`.

## Key Components
*   **`OnboardingScreen`**: The primary entry point that handles side-effect collection and Activity Result Launchers for system-level interactions.
*   **`WizardPager`**: A reusable, horizontally-scrolling component designed for setup flows, featuring custom alpha-based page transitions.
*   **`AnimatedCookieShape`**: A custom graphic component that uses `androidx.graphics.shapes` to morph between star-like polygons, providing a unique visual identity during onboarding.

## Android TV
The wizard adapts to leanback / TV hardware, which frequently lacks a microphone and ships stripped ROMs without every system-settings screen. Form factor is detected via `LocalFormFactor.current == FormFactor.TV`. _(@since 1.2.0)_

*   **`RECORD_AUDIO` skipped on TV**: `RECORD_AUDIO` (used by the WebRTC camera stack) is dropped from the "all permissions granted" gate in `OnboardingContent` when running on TV, since TV boxes commonly have no microphone and would otherwise strand the user on the permissions slide. The `CAMERA` permission stays required (Camera2 external motion needs it), and the audio toggle row itself is still shown.
*   **Conditional permission-screen availability**: The overlay, device-admin, and write-settings rows depend on the device actually exposing the matching system-settings `Intent`. `OnboardingContent` probes each with `resolveActivity(...)` up front; unavailable rows are hidden in `PermissionsList` and dropped from the completion gate so the user is never blocked by an un-grantable requirement. `OnboardingScreen` additionally wraps every optional system-screen launch (`launchSystemScreen`) in a `try/catch` for `ActivityNotFoundException`, showing an `error_permission_screen_unavailable` snackbar instead of crashing.
*   **D-pad wizard focus**: `WizardPager` remembers a `FocusRequester` that only the active page attaches (`focusRequester(...).focusGroup()`), and a `LaunchedEffect` keyed on the current page requests focus after a short `FOCUS_REQUEST_DELAY_MS` (200 ms) delay to avoid racing the slide transition. On TV this lands D-pad focus on the current slide's first focusable (permission toggle or URL field); slides with no focusable content leave focus for the user to reach the Prev/Next buttons. The whole effect is gated on `isTv`, so it is a no-op on mobile (touch).

## Dependencies
*   **`presentation-core-ui`**: For buttons, input items, and animation hosts.
*   **`presentation-core-platform`**: For access to the `ApplicationDeviceAdminReceiver`.
*   **`presentation-core-styling`**: For `FormFactor` / `LocalFormFactor` TV detection.
*   **`domain-usecase-api`**: For reading/writing configuration state.
*   **Orbit MVI**: For unidirectional data flow management.
