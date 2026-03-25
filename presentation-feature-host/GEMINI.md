# Module: presentation-feature-host

## Overview
This module serves as the primary entry point and host for the entire application. it contains the main activity that bootstraps the UI, manages system-level configurations, and orchestrates the initial navigation flow.

## Responsibilities
*   **Application Bootstrapping**: `HostActivity` serves as the single activity in the app's Single Activity Architecture.
*   **Splash Screen Management**: Handles the display and dismissal of the splash screen, including custom exit animations and synchronization with initial data loading.
*   **Initial Route Selection**: Determines whether to show the Onboarding flow or the Main dashboard based on the user's completion status.
*   **System UI & Interaction**: Enforces immersive mode (hiding status/navigation bars) and globally blocks the back gesture/button to maintain kiosk integrity.
*   **Auto-Start**: Contains the `BootReceiver` to automatically launch the kiosk activity when the device finishes booting.
*   **Theme Observation**: Observes the global theme preference and applies it to the entire application host.

## Architecture
This module acts as the shell for the application's Compose-based UI. It integrates the navigation implementation and serves as the bridge between the Android Activity lifecycle and the Composable world.

### Key Components
*   **`presentation.feature.host.source.host.HostActivity`**: The central coordinator for the app's lifecycle and UI hosting.
*   **`presentation.feature.host.source.receiver.BootReceiver`**: Ensures the kiosk is always running by starting the activity on system boot.

## Dependencies
*   **`presentation-core-navigation-impl`**: To host the application's navigation graph.
*   **`presentation-core-styling`**: For global theme application.
*   **`domain-usecase-api`**: For checking onboarding status and observing theme settings.
