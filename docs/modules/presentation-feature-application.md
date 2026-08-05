# presentation-feature-application

This module provides the interface for selecting and managing applications that are allowed to run within the Kiosk environment.

## Features

*   **Smart Discovery**: Automatically lists all installed apps with a valid launch intent.
*   **Intuitive UI**: Uses the "Chosen First" sorting logic to keep active applications at the top of the list.
*   **Visual Consistency**: Leverages the Design System's `ApplicationListItem` for a native, high-quality look.
*   **Smooth Animations**: Implements sequential entrance animations for a modern user experience.
*   **Android TV Ready**: Adapts the list to a capped, centred, overscan-safe layout on TV form factors, with D-pad-stable focus.

## Usage

This screen is typically accessed from the **Settings** menu. When a user toggles an application, it is immediately persisted to the local database via the domain layer.

## Architecture

The module follows the **MVI (Model-View-Intent)** pattern implemented with the **Orbit** framework. This ensures a unidirectional data flow and predictable state transitions.

*   **State**: Immutable representation of the screen's UI.
*   **Intent**: Dispatched by the UI to request changes or perform actions.
*   **Side Effect**: One-time events like navigation or showing a snackbar.

## Internal Structure

*   `source/application/`:
    *   `ApplicationScreen`: Entry point and side-effect handler.
    *   `ApplicationViewModel`: State coordinator and Use Case orchestrator.
    *   `ApplicationContent`: UI layout and lazy list implementation.
    *   `ApplicationContract`: Definition of State and Side Effects.
    *   `ApplicationIntent`: Definition of user actions.
*   `di/`: Koin module for dependency injection.

## Android TV

On a TV form factor (`LocalFormFactor.current == FormFactor.TV`) the screen adapts its layout:

*   **Capped, centred list**: The application list body is constrained to a maximum readable width (~840.dp) and centred rather than stretching rows across the full 16:9 panel. Mobile keeps the full-width layout.
*   **Overscan insets**: Horizontal overscan padding keeps content clear of the bezel dead zone.
*   **Full-width header**: The back-button header row stays full-width; only the list body is capped and centred.
*   **D-pad focus stability**: `LazyColumn` items use `packageName` as a stable key, so when selecting an app reorders the list (chosen items float to the top), Compose moves the same composable — and its D-pad focus — to the item's new position instead of leaving focus stranded at the old index.
*   **TV preview**: A dedicated `Devices.TV_1080p` preview renders the content with `FormFactor.TV`.
