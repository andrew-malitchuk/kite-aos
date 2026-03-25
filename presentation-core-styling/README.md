# presentation-core-styling

The styling engine for the Home Kiosk application.

## Features
- **Semantic Theming**: Uses a custom `ThemeColor` palette that maps to UI roles rather than specific hex codes.
- **Dynamic Color**: Full support for Android 12+ Material You dynamic theming.
- **Modern Typography**: Combines **Outfit** (for headers) and **Plus Jakarta Sans** (for body text) for a modern, readable interface.
- **8dp Grid System**: Standardized spacing and sizing tokens based on a 4/8dp baseline.

## Structure
- `core/`: Base data classes and the main `Theme` access object.
- `source/attribute/`: Default values for font sizes, spacing, and typography.
- `source/attribute/color/`: Pre-defined Light and Dark color palettes.
- `source/provider/`: Internal `CompositionLocal` definitions.
- `source/theme/`: The `AppTheme` implementation.

## Adding New Tokens
1. Define the token in the appropriate data class in `core/`.
2. Add the default value in `source/attribute/`.
3. If it's a color, update both `LightColor.kt` and `DarkColor.kt`.
