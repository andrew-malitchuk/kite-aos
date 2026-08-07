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

## Android TV / Adaptive Form-Factor

_Since `1.2.0`._ The styling engine adapts to remote-driven, across-the-room devices (Android TV) and large expanded windows.

- **`FormFactor`** (`core/`): Enum with `MOBILE` and `TV`. Describes the *input model and viewing distance* of the device, which is deliberately distinct from a `WindowSizeClass` (available space). A TV is remote-driven and viewed from across the room. Both signals feed `Theme.is10Foot`.
- **`LocalFormFactor`**: `CompositionLocal<FormFactor>`, defaults to `MOBILE` so previews and tests don't crash.
- **`LocalWindowSizeClass`**: `CompositionLocal<WindowSizeClass?>`, defaults to `null` ("unknown / compact").
- **`Theme.is10Foot`**: `true` when the form-factor is `TV` **or** the window width size class is `Expanded` (large tablets / foldables benefit too); a `null` size class counts as not expanded.

### How it wires together
Both CompositionLocals are provided at the host level, above `AppTheme`: `LocalFormFactor` from `AppConfig.isTv`, and `LocalWindowSizeClass` from `calculateWindowSizeClass(activity)`. When `Theme.is10Foot` is `true`, `AppTheme` up-scales font sizes, sizing, line heights, spacing, and typography by `TEN_FOOT_SCALE` (`1.3f`) through the same theme CompositionLocals, so most screens adapt with no per-screen changes.

The `WindowSizeClass` API comes from the `material3-window-size-class` dependency (`androidx.compose.material3`, version via the Compose BOM).

## Adding New Tokens
1. Define the token in the appropriate data class in `core/`.
2. Add the default value in `source/attribute/`.
3. If it's a color, update both `LightColor.kt` and `DarkColor.kt`.
