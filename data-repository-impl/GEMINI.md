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

## Dependencies
*   **`domain-repository-api`**: The interfaces being implemented.
*   **`data-database-api`**, **`data-preferences-api`**, **`data-platform-api`**, **`data-mqtt-api`**: The data sources used by the repositories.
*   **`domain-core`**: The domain models being returned.
