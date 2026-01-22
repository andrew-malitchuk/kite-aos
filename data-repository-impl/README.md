# data-repository-impl

Concrete implementation of the repository interfaces defined in `domain-repository-api`. This module orchestrates various data sources (Database, Preferences, Platform, MQTT) to provide a unified data access layer for the domain.

## Features

- **Source Orchestration**: Combines data from Room databases, DataStore preferences, and Android system APIs.
- **Data Mapping**: Uses bidirectional mappers to convert between Data Layer Resources (DTOs) and Domain Layer Models.
- **Reactive Streams**: Provides `Flow`-based implementations for real-time data observation across the application.

## Implementation Details

The repositories in this module are implemented as singletons and bound to their respective interfaces using Koin annotations.

### Key Repositories

- **`ApplicationRepositoryImpl`**: Manages applications by coordinating `ApplicationDatabaseSource` (loading persisted selection) and `ApplicationPlatformSource` (getting system-installed apps).
- **`ConfigureRepositoryImpl`**: Manages all user settings by interacting with multiple `PreferenceSource` implementations.
- **`MqttRepositoryImpl`**: Handles MQTT connectivity and telemetry reporting.
