# presentation-core-localisation

Centralized module for managing application string resources and dynamic language switching for the "kite-aos" project.

## Features

- **Multi-language Support**: Contains localized string resources for English (`en`) and Ukrainian (`uk`).
- **Dynamic Language Switching**: Provides a utility to change the application's locale at runtime using `AppCompatDelegate`.
- **Centralized Resources**: Acts as the single source of truth for all UI text, ensuring consistency across feature modules.
- **EN/UK Parity**: Every string key is maintained in both `values` (English) and `values-uk` (Ukrainian); new keys are added to both files together.

### String Categories
Strings are grouped by feature. Recent additions include:
- **Camera source** (`@since 1.4.0`): `settings_camera_source`, `settings_camera_source_auto` / `_front` / `_rear` / `_external`, and `hint_camera_source` — labels for the Settings camera-source selector used by motion detection and MJPEG streaming.
- **Android TV D-pad hints**: `tv_hint_navigate`, `tv_hint_select`, `tv_hint_back_to_list` — the on-screen remote-control legend for the TV settings layout.
- **Device-restricted screen warning**: `error_permission_screen_unavailable` — shown when a screen isn't available on the current device.

## Usage

### Accessing Strings
All feature modules should depend on this module and access strings via the generated `R` class:

```kotlin
import presentation.core.localisation.R

// In Compose
Text(text = stringResource(id = R.string.settings_title))
```

### Switching Language
Use the provided delegate function to update the application's locale:

```kotlin
import presentation.core.localisation.source.delegate.changeAppLanguage

changeAppLanguage("uk") // Switches the app to Ukrainian
```
