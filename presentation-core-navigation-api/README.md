# presentation-core-navigation-api

This module defines the navigation contracts and destinations for the "kite-aos" project.

## Features

- **AppNavigator**: An abstract interface for performing navigation actions, supporting modes like single-top and task clearing.
- **Destinations**: Strongly-typed destinations (using `Destination` sealed class) for all major application screens, defined as `data object`s.
- **CompositionLocals**: Easy access to the navigator and back actions throughout the Compose tree via `LocalAppNavigator` and `LocalBackAction`.

## Usage

### Navigating

To navigate from a Composable, obtain the `AppNavigator` from `LocalAppNavigator`:

```kotlin
val navigator = LocalAppNavigator.current

// Basic navigation
navigator?.navigate(Destination.Settings)

// Navigation with options
navigator?.navigate(Destination.Main, AppNavigator.NavOptions.ClearTask)
```

### Back Action

You can use `popBackStack()` or the legacy `backAction()`:

```kotlin
navigator?.popBackStack()
```

