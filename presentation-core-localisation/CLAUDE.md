# Module: presentation-core-localisation

## Overview
This module is responsible for the internationalization (i18n) and localization (l10n) of the application. It stores all user-facing strings and provides the mechanism to switch languages dynamically.

## Responsibilities
*   **String Management**: Centralizes all string resources to avoid duplication and facilitate translation.
*   **Locale Management**: Provides a standard way to change the application's language programmatically using the AndroidX `AppCompatDelegate` API.

## Architecture
The module follows the standard Android localization pattern using `res/values/strings.xml` for the default language (English) and `res/values-uk/strings.xml` for Ukrainian.

### Key Components
*   **`changeAppLanguage(languageCode: String)`**: A utility function that wraps `AppCompatDelegate.setApplicationLocales`. This method is preferred over older manual configuration updates as it integrates with the system-level per-app language settings introduced in modern Android versions.
*   **Resource Categories**: Strings are organized by feature within the XML files (e.g., Onboarding, Settings, Permissions, MQTT) to maintain readability. Recent feature-string categories include:
    *   **Camera source** (`@since 1.4.0`): `settings_camera_source` (format string), `settings_camera_source_auto` / `_front` / `_rear` / `_external`, and the subtitle `hint_camera_source` — labels for the Settings camera-source selector (motion detection / MJPEG streaming).
    *   **Android TV D-pad hints**: `tv_hint_navigate`, `tv_hint_select`, `tv_hint_back_to_list` — the `↕ / OK / ←` legend shown on the TV settings layout.
    *   **Device-restricted screen warning**: `error_permission_screen_unavailable` — shown when a settings screen isn't available on the current device form factor.
*   **EN/UK parity**: Every key exists in both `values` (English) and `values-uk` (Ukrainian); new keys are always added to both files together to keep the two locales in sync.

## Dependencies
*   **`androidx.appcompat`**: For `AppCompatDelegate` support.
*   **`androidx.core`**: For `LocaleListCompat`.
