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
*   **`AboutScreen`**: The entry point Composable that integrates the ViewModel with the UI and handles side effects like navigation and starting external activities. Social-link `ACTION_VIEW` intents are wrapped in `runCatching` so a missing browser (common on Android TV) never crashes the kiosk (see Android TV below).
*   **`AboutContent`**: The layout implementation using Jetpack Compose, featuring entry animations for UI elements.
*   **`AnimatedCookieShape`**: A custom Composable that uses `Morph` and `RoundedPolygon` to create a rotating and morphing star-like shape.
*   **`AboutViewModel`**: An Orbit-powered ViewModel that handles business logic and navigation side effects.

## Android TV
The screen adapts its layout and link handling when running on a TV form factor (`LocalFormFactor.current == FormFactor.TV`):

*   **Capped, centred content**: The content body is constrained to a maximum readable width (~720.dp) and centred rather than stretching a lone column across the full 16:9 panel — the screen is read from across the room. Mobile keeps the full-width layout.
*   **Overscan insets**: Horizontal overscan padding is applied so text and the logo stay clear of the bezel dead zone.
*   **Full-width header**: The header row (back button) stays full-width and hard-left; only the body is capped and centred. This mirrors the pattern used in `presentation-feature-application`.
*   **Browser-safe social links**: Many Android TV boxes ship without a general-purpose browser, so `ACTION_VIEW` on a web URL throws `ActivityNotFoundException`. `AboutScreen` wraps `startActivity` in `runCatching`, logging a warning instead of crashing; on phones/tablets this is the normal happy path.
*   **TV preview**: A dedicated `@Preview(device = Devices.TV_1080p)` renders `AboutContent` with `LocalFormFactor` provided as `FormFactor.TV`.

## Dependencies
*   **`domain-core`**: For base models.
*   **`presentation-core-ui`**: For shared UI components (Atoms, Molecules, Organisms) and styling.
*   **`presentation-core-navigation-api`**: For navigating back in the app's flow.
*   **`presentation-core-localisation`**: For managing string resources.
*   **Orbit MVI**: For state management.
*   **Koin**: For dependency injection using annotations.
