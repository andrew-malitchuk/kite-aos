# Module: data-preferences-api

## Overview
This module defines the API layer for managing application preferences. It provides abstract interfaces for data sources and defines the data models (resources) for various user settings. This module contains no implementation logic, ensuring a strict decoupling between the definition of preferences and their physical storage mechanism.

## Responsibilities
*   **Contract Definition**: Defines the `PreferenceSource` interface, establishing a standard pattern for reactive preference management (get, set, observe).
*   **Data Models**: Defines strongly-typed data classes (`Resource`) representing specific preference sets.
*   **Specialized API**: Provides specific interfaces for different feature settings (Theme, Dashboard, MQTT, etc.).

## Architecture
This is a pure API module following Clean Architecture principles. It depends only on `data-core` for the base `Resource` marker.

### Key Interfaces
*   **`data.preferences.api.source.datasource.base.PreferenceSource<T>`**: The foundation for all preference access.
    *   `getData(): T?`: Retrieves the current snapshot.
    *   `setData(data: T?)`: Updates the current value.
    *   `observeData(): Flow<T?>`: Reactive stream of updates.

### Feature-Specific Sources
*   `DashboardPreferenceSource`: Kiosk URLs and whitelist.
*   `MoveDetectorPreferenceSource`: Sensitivity and delay settings for motion detection.
*   `MqttPreferenceSource`: Broker connection details and friendly naming.
*   `ThemePreferenceSource`: Visual mode (Light/Dark/MaterialU).
*   `OnboardingPreferenceSource`: Setup completion state.

## Dependencies
*   **`data-core`**: Provides the foundational `Resource` markers.
