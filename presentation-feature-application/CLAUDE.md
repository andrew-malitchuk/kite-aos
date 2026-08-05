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
*   **`ApplicationContent`**: The layout implementation using `LazyColumn` for performance and `SafeContainer` for consistent system bar handling. The `LazyColumn` uses `packageName` as a stable item key so D-pad focus follows items when the list reorders (see Android TV below).
*   **Animated Entry**: Utilizes `AnimationSequenceHost` and `AnimatedItem` from the UI core to provide smooth, choreographed entrance animations for the header and the list.

## Android TV
The screen adapts its layout when running on a TV form factor (`LocalFormFactor.current == FormFactor.TV`):

*   **Capped, centred list**: The application list body is constrained to a maximum readable width (~840.dp) and centred instead of stretching rows across the full 16:9 panel. Mobile keeps the untouched full-width layout.
*   **Overscan insets**: Horizontal overscan padding is applied so no content lands in the bezel dead zone on TV displays.
*   **Full-width header**: The header row (back button) stays full-width and hard-left; only the list body is capped and centred.
*   **D-pad focus stability**: List items use `packageName` as a stable key. Selecting an app reorders the list (chosen items float to the top); the stable key lets Compose move the same composable — and its D-pad focus — to the item's new position instead of stranding focus on whatever now sits at the old index.
*   **TV preview**: A dedicated `@Preview(device = Devices.TV_1080p)` renders `ApplicationContent` with `LocalFormFactor` provided as `FormFactor.TV`.

## Dependencies
*   **`presentation-core-ui`**: For consistent atoms (Buttons, Dividers) and organisms (Animation Host).
*   **`domain-usecase-api`**: For accessing the application management business logic.
*   **Orbit MVI**: For predictable state management.
