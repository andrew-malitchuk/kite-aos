# presentation-core-localisation

Centralized module for managing application string resources and dynamic language switching for the "kite-aos" project.

## Features

- **Multi-language Support**: Contains localized string resources for English (`en`) and Ukrainian (`uk`).
- **Dynamic Language Switching**: Provides a utility to change the application's locale at runtime using `AppCompatDelegate`.
- **Centralized Resources**: Acts as the single source of truth for all UI text, ensuring consistency across feature modules.

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
