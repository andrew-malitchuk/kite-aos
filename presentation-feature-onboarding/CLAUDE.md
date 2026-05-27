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

## Dependencies
*   **`presentation-core-ui`**: For buttons, input items, and animation hosts.
*   **`presentation-core-platform`**: For access to the `ApplicationDeviceAdminReceiver`.
*   **`domain-usecase-api`**: For reading/writing configuration state.
*   **Orbit MVI**: For unidirectional data flow management.
