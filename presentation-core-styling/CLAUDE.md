# Module: presentation-core-styling

## Overview
This module defines the central design system and styling foundation for the application. It provides a structured approach to themes, colors, typography, and spacing, ensuring a consistent visual language across all screens.

## Responsibilities
*   **Theme Management**: Defines the `AppTheme` composable which acts as the top-level styling provider.
*   **Design Tokens**: Manages semantic tokens for colors, typography, spacing, sizes, and line heights.
*   **Multi-Theme Support**: Implements Light, Dark, and Material You (Dynamic Color) theme variants.
*   **Font Management**: Bundles and configures custom typefaces (Outfit and Plus Jakarta Sans).
*   **Adaptive Form-Factor**: Detects TV / large-screen ("10-foot") contexts and up-scales the design tokens so screens adapt with no per-screen changes.

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

## Android TV / Adaptive Form-Factor

Since `1.2.0` the design system adapts to remote-driven, across-the-room devices (Android TV / leanback) and large expanded windows without requiring per-screen work.

### Key Components
*   **`presentation.core.styling.core.FormFactor`**: Enum (`MOBILE`, `TV`) describing the *input model and viewing distance* of the device. This is deliberately distinct from `WindowSizeClass` (which describes available space): a TV is remote-driven and viewed from across the room even though its window may report an ordinary size. Both signals feed `Theme.is10Foot`.
*   **`presentation.core.styling.core.LocalFormFactor`**: `CompositionLocal<FormFactor>` defaulting to `FormFactor.MOBILE` so Composable previews and un-wrapped test trees don't crash.
*   **`presentation.core.styling.core.LocalWindowSizeClass`**: `CompositionLocal<WindowSizeClass?>` defaulting to `null` ("unknown / compact"), because a `WindowSizeClass` cannot be derived without an `Activity`.
*   **`presentation.core.styling.core.Theme.is10Foot`**: `@Composable Boolean` that is `true` when `LocalFormFactor` is `TV` **or** `LocalWindowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded` (large tablets / foldables benefit too). The `null` size class is treated as "not expanded".

### Host wiring

Both CompositionLocals are provided at the host level, **above** `AppTheme`:
*   `LocalFormFactor` is provided from `AppConfig.isTv`.
*   `LocalWindowSizeClass` is provided from `calculateWindowSizeClass(activity)`.

`AppTheme` then reads `Theme.is10Foot` internally. When `true`, it up-scales font sizes, sizing, line heights, spacing, and typography by `TEN_FOOT_SCALE` (`1.3f`) via `scaledBy(...)` before providing them through the same theme CompositionLocals. Consumers reading `Theme.*` adapt automatically; read `Theme.is10Foot` directly only where a screen needs a genuinely different layout arrangement or focus affordance.

## Dependencies
*   **`domain-core`**: For the `ThemeModel` definition.
*   **Android Material 3**: For dynamic color integration.
*   **`material3-window-size-class`** (`androidx.compose.material3`, version via the Compose BOM): Provides the `WindowSizeClass` API. Exposed with `api(...)` so consumers reading `LocalWindowSizeClass` get the type transitively.
*   **Jetpack Compose**: Core UI framework.
