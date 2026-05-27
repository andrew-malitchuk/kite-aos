# Module: presentation-feature-settings

## Overview
This feature module provides the "Settings" screen for the application. It allows users to configure global settings such as the UI theme, dock position, kiosk dashboard URLs, motion detector parameters, and MQTT telemetry configuration.

## Responsibilities
*   **User Interface**: Renders a comprehensive settings interface using Jetpack Compose and the project's shared UI kit.
*   **State Management**: Implements the MVI (Model-View-Intent) pattern using the Orbit framework to manage settings state and asynchronous updates.
*   **Domain Integration**: Orchestrates domain logic by calling multiple use cases to fetch and persist user preferences.
*   **Localization**: Supports dynamic language switching, including deep-linking to system language settings on modern Android versions.
*   **Navigation**: Handles navigation events to external screens like "About" and "Application Selection".

## Architecture
This module follows the standard feature architecture of the project:
*   **MVI (Orbit)**:
    *   **`SettingsState`**: Represents the full configuration state (Theme, Dock, MQTT, etc.).
    *   **`SettingsIntent`**: Encapsulates user actions (e.g., `OnSetThemeIntent`, `OnSetDashboardIntent`).
    *   **`SettingsSideEffect`**: Manages one-off events like navigation and error reporting.
*   **ViewModel**: `SettingsViewModel` acts as the container host, debouncing high-frequency updates (like text input) before persisting them via use cases.
*   **Composables**:
    *   `SettingsScreen`: The entry point that integrates with the ViewModel and handles side effects.
    *   `SettingsContent`: The main UI implementation featuring smooth entry animations and a structured list layout.

## Key Components
*   **`SettingsViewModel`**: The primary logic controller, interacting with over 10 different domain use cases.
*   **`SettingsContent`**: A complex layout utilizing specialized UI molecules like `NumberInputListItem` and `ThemeListItem`.
*   **`LanguageSelectionDialog`**: An internal component for in-app language switching when system settings are unavailable.

## Dependencies
*   **`domain-usecase-api`**: For all business logic operations.
*   **`presentation-core-ui`**: For consistent design system components.
*   **`presentation-core-navigation-api`**: For navigating between features.
*   **Orbit MVI**: Core state management library.
