# Module: presentation-core-navigation-api

## Overview
This module defines the API layer for navigation within the application. It provides the contracts for screen transitions and the definition of all available destinations.

## Responsibilities
*   **Navigation Contract**: Defines the `AppNavigator` interface for navigating between screens and handling back actions.
*   **Destination Definitions**: Defines the `Destination` sealed class, which represents all reachable screens in the application (Onboarding, Main, Settings, About, Application).
*   **Composition Locals**: Provides `LocalAppNavigator` and `LocalBackAction` for easy access to navigation functionality within Jetpack Compose.

## Architecture
This is a pure API module that ensures decoupling between feature modules and the navigation implementation. It relies on the Navigation 3 library for its core types (`NavKey`).

### Key Interfaces
*   **`AppNavigator`**:
    *   `navigate(destination: Destination, options: NavOptions)`: Navigates with optional strategies (Default, SingleTop, ClearTask).
    *   `popBackStack()`: Removes the current screen from the backstack.
    *   `backAction()`: Alias for backward compatibility.

### Key Models
*   **`Destination`**: A sealed class of `data object`s representing all screens. Each destination is serializable for state preservation.

## Dependencies
*   **`androidx.navigation:navigation-runtime`**: Core navigation primitives.
*   **`kotlinx.serialization`**: For serializing destinations.
