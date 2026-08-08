# Module: data-repository-impl

## Overview
This module provides the concrete implementations for the repository interfaces defined in `domain-repository-api`. It sits between the domain layer and the various data sources, handling data orchestration and mapping.

## Responsibilities
*   **Interface Implementation**: Implements `ApplicationRepository`, `ConfigureRepository`, and `MqttRepository`.
*   **Data Orchestration**: Coordinates between multiple data sources (e.g., combining database records with system-installed apps).
*   **Mapping**: Converts between domain models (`ApplicationModel`, `DashboardModel`) and data resources (`ApplicationDatabase`, `DashboardPreference`) using specialized mappers.
*   **Dependency Injection**: Configures Koin modules to bind implementations to their respective domain interfaces.

## Architecture
This module follows the Repository Pattern implementation. It depends on various `-api` modules for data sources and the `domain-repository-api` for the contracts it fulfills.

### Key Components
*   **Repositories**:
    *   `ApplicationRepositoryImpl`: Combines database and platform sources.
    *   `ConfigureRepositoryImpl`: Orchestrates multiple preference sources (Theme, Dock, Dashboard, etc.).
    *   `MqttRepositoryImpl`: Bridges domain MQTT calls to the telemetry and preference sources.
*   **Mappers**: Bidirectional mappers (e.g., `ApplicationDatabaseMapper`, `ThemePreferenceMapper`) that ensure clean separation between data and domain models.

### Camera-Source Preference Plumbing (`@since 1.4.0`)
`ConfigureRepositoryImpl` implements `getCameraSource()`, `setCameraSource(...)` and `observeCameraSource()`, orchestrating `CameraPreferenceSource` (persistence) with the new `CameraPreferenceMapper`. The mapper converts between `CameraSourceModel` and `CameraPreference`, matching the stored mode string against the known `CameraSourceModel` entries and falling back to `CameraSourceModel.Auto` when no match is found (which also covers the empty-string default of a fresh Proto DataStore).

### Screen-State Mapping — `DarkOverlay` (`@since 1.2.0`, Android TV)
`ScreenStateResourceMapper` maps the new `ScreenStateResource.DarkOverlay` both ways to `ScreenStateModel.DarkOverlay`. `DarkOverlay` is a plain dark overlay used as a stand-in for powering the screen off on devices where the app cannot turn the panel off (e.g. Android TV, where device locking is unavailable).

## Dependencies
*   **`domain-repository-api`**: The interfaces being implemented.
*   **`data-database-api`**, **`data-preferences-api`**, **`data-platform-api`**, **`data-mqtt-api`**: The data sources used by the repositories.
*   **`domain-core`**: The domain models being returned.
