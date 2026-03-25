# presentation-core-navigation-impl

This module provides the concrete implementation of the navigation system using the Navigation 3 library.

## Features

- **NavigationHost**: The root component that hosts the application's backstack and maps destinations to their respective screen Composables.
- **AppNavigatorImpl**: The concrete implementation of the `AppNavigator` interface that manages the `NavBackStack`.
- **Centralized Routing**: Orchestrates the navigation between all feature modules (Main, Onboarding, Settings, About, Application).

## Usage

The `NavigationHost` should be placed at the top of the application's UI hierarchy.

```kotlin
NavigationHost(startDestination = Destination.Main)
```

It automatically provides the `AppNavigator` via `CompositionLocalProvider`, making it available to all child Composables.
