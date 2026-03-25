# Module: presentation-feature-about

## Overview
This feature module provides the "About" screen of the application. It displays information about the project, including its title, description, and links to social profiles or the source code repository.

## Responsibilities
*   **Branding & Info**: Displays the application icon (with a custom animated background), title, and project description.
*   **Social Connectivity**: Provides action buttons to open external links (GitHub, LinkedIn, Twitter/X) in a web browser.
*   **Visual Flair**: Implements a unique `AnimatedCookieShape` using the Android Graphics Shapes library to provide a dynamic, morphing background for the app logo.
*   **Navigation**: Handles the "Back" action to return to the previous screen.

## Architecture
The module follows the MVI (Model-View-Intent) architecture pattern using the Orbit framework:
*   **`AboutState`**: Simple state representing the UI (e.g., loading status).
*   **`AboutIntent`**: User actions such as clicking social links or the back button.
*   **`AboutSideEffect`**: One-off events for navigating back or opening external URLs via Intents.
*   **ViewModel**: `AboutViewModel` manages the state and side effects, triggered by intents.

## Key Components
*   **`AboutScreen`**: The entry point Composable that integrates the ViewModel with the UI and handles side effects like navigation and starting external activities.
*   **`AboutContent`**: The layout implementation using Jetpack Compose, featuring entry animations for UI elements.
*   **`AnimatedCookieShape`**: A custom Composable that uses `Morph` and `RoundedPolygon` to create a rotating and morphing star-like shape.
*   **`AboutViewModel`**: An Orbit-powered ViewModel that handles business logic and navigation side effects.

## Dependencies
*   **`domain-core`**: For base models.
*   **`presentation-core-ui`**: For shared UI components (Atoms, Molecules, Organisms) and styling.
*   **`presentation-core-navigation-api`**: For navigating back in the app's flow.
*   **`presentation-core-localisation`**: For managing string resources.
*   **Orbit MVI**: For state management.
*   **Koin**: For dependency injection using annotations.
