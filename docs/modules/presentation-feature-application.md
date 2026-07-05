# presentation-feature-application

This module provides the interface for selecting and managing applications that are allowed to run within the Kiosk environment.

## Features

*   **Smart Discovery**: Automatically lists all installed apps with a valid launch intent.
*   **Intuitive UI**: Uses the "Chosen First" sorting logic to keep active applications at the top of the list.
*   **Visual Consistency**: Leverages the Design System's `ApplicationListItem` for a native, high-quality look.
*   **Smooth Animations**: Implements sequential entrance animations for a modern user experience.

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
