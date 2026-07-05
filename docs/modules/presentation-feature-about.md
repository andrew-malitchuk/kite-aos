# presentation-feature-about

This feature module provides the "About" screen of the application. It displays information about the project, including its title, description, and links to social profiles or the source code repository.

## Features

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

## Usage

The `AboutScreen` is the entry point for this feature. It should be integrated into the application's navigation graph.

```kotlin
AboutScreen()
```
