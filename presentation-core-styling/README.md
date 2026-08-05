# presentation-core-styling

The styling engine for the Home Kiosk application.

## Features
- **Semantic Theming**: Uses a custom `ThemeColor` palette that maps to UI roles rather than specific hex codes.
- **Dynamic Color**: Full support for Android 12+ Material You dynamic theming.
- **Modern Typography**: Combines **Outfit** (for headers) and **Plus Jakarta Sans** (for body text) for a modern, readable interface.
- **8dp Grid System**: Standardized spacing and sizing tokens based on a 4/8dp baseline.
- **Adaptive Form-Factor** _(since 1.2.0)_: Detects Android TV / large-screen ("10-foot") contexts and up-scales tokens so screens adapt automatically.

## Structure
- `core/`: Base data classes and the main `Theme` access object.
- `source/attribute/`: Default values for font sizes, spacing, and typography.
- `source/attribute/color/`: Pre-defined Light and Dark color palettes.
- `source/provider/`: Internal `CompositionLocal` definitions.
- `source/theme/`: The `AppTheme` implementation.

## Android TV / Adaptive Form-Factor

_Since 1.2.0._ The engine adapts to remote-driven, across-the-room devices and large expanded windows.

- **`FormFactor`** (`MOBILE` / `TV`): describes the *input model and viewing distance* (a TV is remote-driven, viewed from across the room) — distinct from `WindowSizeClass`, which is about available space. Both feed `Theme.is10Foot`.
- **`LocalFormFactor`**: `CompositionLocal<FormFactor>`, defaults to `MOBILE` (keeps previews/tests safe).
- **`LocalWindowSizeClass`**: `CompositionLocal<WindowSizeClass?>`, defaults to `null` ("unknown / compact").
- **`Theme.is10Foot`**: `true` when the form-factor is `TV` **or** the window width is `Expanded`.

Both locals are provided at the host level (above `AppTheme`): `LocalFormFactor` from `AppConfig.isTv`, `LocalWindowSizeClass` from `calculateWindowSizeClass(activity)`. When `is10Foot` is `true`, `AppTheme` up-scales spacing, sizing, line heights, font sizes, and typography by `TEN_FOOT_SCALE` (`1.3f`), so most screens adapt with no changes. `WindowSizeClass` comes from the `material3-window-size-class` dependency (version via the Compose BOM).

## Adding New Tokens
1. Define the token in the appropriate data class in `core/`.
2. Add the default value in `source/attribute/`.
3. If it's a color, update both `LightColor.kt` and `DarkColor.kt`.
