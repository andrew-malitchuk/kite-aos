# Module: presentation-core-styling

## Overview
This module defines the central design system and styling foundation for the application. It provides a structured approach to themes, colors, typography, and spacing, ensuring a consistent visual language across all screens.

## Responsibilities
*   **Theme Management**: Defines the `AppTheme` composable which acts as the top-level styling provider.
*   **Design Tokens**: Manages semantic tokens for colors, typography, spacing, sizes, and line heights.
*   **Multi-Theme Support**: Implements Light, Dark, and Material You (Dynamic Color) theme variants.
*   **Font Management**: Bundles and configures custom typefaces (Outfit and Plus Jakarta Sans).

## Architecture
The styling system is built on Jetpack Compose `CompositionLocal`s, allowing any UI component to access theme attributes without prop-drilling.

### Key Components
*   **`presentation.core.styling.core.Theme`**: A singleton object providing easy access to the current theme values (`Theme.color`, `Theme.typography`, etc.).
*   **`presentation.core.styling.source.theme.AppTheme`**: The primary entry point for applying the theme to the UI tree. It supports three modes: `Light`, `Dark`, and `MaterialU`.
*   **`presentation.core.styling.core.ThemeColor`**: A data class containing semantic color slots (brand, canvas, ink, surface, outline, status).
*   **`presentation.core.styling.core.provideDynamicThemeColor`**: Logic for extracting colors from the Android system (Material You) and mapping them to the internal theme structure.

## Usage
To use the theme in a feature, wrap the root Composable with `AppTheme`:

```kotlin
AppTheme(mode = themeMode) {
    // Your UI content
}
```

Access tokens within Composables:

```kotlin
Text(
    text = "Hello",
    color = Theme.color.inkMain,
    style = Theme.typography.body
)
```

## Dependencies
*   **`domain-core`**: For the `ThemeModel` definition.
*   **Android Material 3**: For dynamic color integration.
*   **Jetpack Compose**: Core UI framework.
