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
*   **Resource Categories**: Strings are organized by feature within the XML files (e.g., Onboarding, Settings, Permissions, MQTT) to maintain readability.

## Dependencies
*   **`androidx.appcompat`**: For `AppCompatDelegate` support.
*   **`androidx.core`**: For `LocaleListCompat`.
