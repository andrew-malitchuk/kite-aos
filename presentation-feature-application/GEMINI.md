# Module: presentation-feature-application

## Overview
This feature module provides the "Application Selection" screen. It allows users to browse all applications installed on the device that have a launch intent and select which ones should be available within the kiosk mode's control drawer.

## Responsibilities
*   **Application Discovery**: Loads the complete list of system applications via the domain layer (orchestrated by the repository which combines database records with platform information).
*   **Selection Management**: Handles user intents to add (save) or remove applications from the "chosen" list.
*   **Stateful List**: Displays applications in a reactive list, sorting them so that selected applications appear at the top.
*   **Navigation**: Manages the back action to return to the Settings or Main screen.

## Architecture
The module strictly follows the **MVI (Model-View-Intent)** pattern using the **Orbit** framework:
*   **`ApplicationState`**: Tracks the loading status, errors, and the list of application models.
*   **`ApplicationIntent`**: Captures user actions like `SaveApplication`, `RemoveApplication`, and `OnBackClick`.
*   **`ApplicationSideEffect`**: Manages one-off events such as navigation and showing error messages via the Design System's snackbar.
*   **`ApplicationViewModel`**: The business logic coordinator that interacts with `LoadApplicationsUseCase`, `SaveApplicationUseCase`, and `RemoveApplicationUseCase`.

## Key Components
*   **`ApplicationScreen`**: The primary entry point Composable that integrates the ViewModel, collects state, and handles navigation/UI side effects.
*   **`ApplicationContent`**: The layout implementation using `LazyColumn` for performance and `SafeContainer` for consistent system bar handling.
*   **Animated Entry**: Utilizes `AnimationSequenceHost` and `AnimatedItem` from the UI core to provide smooth, choreographed entrance animations for the header and the list.

## Dependencies
*   **`presentation-core-ui`**: For consistent atoms (Buttons, Dividers) and organisms (Animation Host).
*   **`domain-usecase-api`**: For accessing the application management business logic.
*   **Orbit MVI**: For predictable state management.
