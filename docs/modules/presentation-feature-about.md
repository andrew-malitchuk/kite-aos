# presentation-feature-about

This feature module provides the "About" screen of the application. It displays information about the project, including its title, description, and links to social profiles or the source code repository.

## Features

*   **Branding & Info**: Displays the application icon (with a custom animated background), title, and project description.
*   **Social Connectivity**: Provides action buttons to open external links (GitHub, LinkedIn, Twitter/X) in a web browser.
*   **Visual Flair**: Implements a unique `AnimatedCookieShape` using the Android Graphics Shapes library to provide a dynamic, morphing background for the app logo.
*   **Navigation**: Handles the "Back" action to return to the previous screen.
*   **Android TV Ready**: Adapts to a capped, centred, overscan-safe layout on TV form factors, with browser-safe social links.

## Architecture

The module follows the MVI (Model-View-Intent) architecture pattern using the Orbit framework:

*   **`AboutState`**: Simple state representing the UI (e.g., loading status).
*   **`AboutIntent`**: User actions such as clicking social links or the back button.
*   **`AboutSideEffect`**: One-off events for navigating back or opening external URLs via Intents.
*   **ViewModel**: `AboutViewModel` manages the state and side effects, triggered by intents.

## Android TV

On a TV form factor (`LocalFormFactor.current == FormFactor.TV`) the screen adapts its layout and link handling:

*   **Capped, centred content**: The content body is constrained to a maximum readable width (~720.dp) and centred rather than stretching a lone column across the full 16:9 panel. Mobile keeps the full-width layout.
*   **Overscan insets**: Horizontal overscan padding keeps text and the logo clear of the bezel dead zone.
*   **Full-width header**: The back-button header row stays full-width; only the body is capped and centred — the same pattern used in `presentation-feature-application`.
*   **Browser-safe social links**: Many Android TV boxes ship without a general-purpose browser, so `ACTION_VIEW` on a web URL throws `ActivityNotFoundException`. `AboutScreen` wraps the launch in `runCatching`, logging a warning instead of crashing the kiosk.
*   **TV preview**: A dedicated `Devices.TV_1080p` preview renders the content with `FormFactor.TV`.

## Usage

The `AboutScreen` is the entry point for this feature. It should be integrated into the application's navigation graph.

```kotlin
AboutScreen()
```
