# Module: presentation-core-navigation-impl

## Overview
This module implements the navigation logic defined in `presentation-core-navigation-api`. It acts as the orchestrator for all feature screens, managing the backstack and the UI tree.

## Responsibilities
*   **Routing Implementation**: Implements `AppNavigatorImpl` using Navigation 3's `NavBackStack`.
*   **UI Orchestration**: Provides the `NavigationHost` Composable, which maps `Destination` objects to their respective feature screens.
*   **State Management**: Manages the navigation backstack state and provides navigation actions to the entire UI tree via `CompositionLocalProvider`.

## Architecture
The implementation relies on the experimental Navigation 3 library, providing a type-safe and declarative way to manage screen transitions.

### Key Components
*   **`NavigationHost`**: The entry point for the application's UI. It initializes the `NavBackStack` and defines the mapping between `Destination` instances and screen Composables.
*   **`AppNavigatorImpl`**: Wraps the `NavBackStack` to provide the simple `navigate` and `backAction` API defined in the navigation contract.

## Routing Table
The following features are integrated into the `NavigationHost`:
*   `Destination.Onboarding` -> `OnboardingScreen`
*   `Destination.Main` -> `MainScreen`
*   `Destination.Settings` -> `SettingsScreen`
*   `Destination.About` -> `AboutScreen`
*   `Destination.Application` -> `ApplicationScreen`

## Dependencies
*   **`presentation-core-navigation-api`**: Implements the contracts from here.
*   **Feature Modules**: Depends on all presentation-feature modules to display their screens.
*   **`androidx.navigation3`**: The underlying navigation engine.
