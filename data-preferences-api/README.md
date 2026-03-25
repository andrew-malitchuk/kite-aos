# data-preferences-api

Public contracts and data models for application preferences.

## Usage

Feature modules should depend on this module to access or observe user settings without being tied to the underlying storage implementation (DataStore).

### Observing Preferences
```kotlin
val themeFlow = themePreferenceSource.observeData()
```

### Updating Preferences
```kotlin
themePreferenceSource.setData(ThemePreference(mode = "dark"))
```

## Structure

*   `source/datasource/`: Specialized interfaces for each setting category.
*   `source/resource/`: Data models representing the settings structure.
