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
- **`ConfigureRepositoryImpl`**: Manages all user settings by interacting with multiple `PreferenceSource` implementations. `@since 1.4.0` it also exposes `getCameraSource()`/`setCameraSource()`/`observeCameraSource()`, orchestrating `CameraPreferenceSource` with the new `CameraPreferenceMapper` (falls back to `CameraSourceModel.Auto` for unknown/empty stored modes).
- **`MqttRepositoryImpl`**: Handles MQTT connectivity and telemetry reporting. Forwards the device `model` (`"tv"`/`"tablet"`) from `connect(...)` to the telemetry source for form-factor-aware Home Assistant registration. `@since 2.1.0` it also translates the domain `MqttDiagnosticEntityModel` enum into the data layer's plain topic ids, and `@since 2.2.0` forwards `purgeDiscovery()`, `sendDashboardState(...)` and `sendCompanionTelemetry(...)`.

### Resilience & Interaction Settings (`@since 2.2.0`)

`ConfigureRepositoryImpl` gained `get`/`set`/`observe` triplets for two new settings groups, backed by `ResiliencePreferenceMapper` and `InteractionPreferenceMapper`:

- **`ResilienceModel`** — the unattended-operation safeguards (crash auto-relaunch, dashboard connection monitor, memory recovery, WiFi lock, scheduled reload + hour).
- **`InteractionModel`** — inactivity page reset and the volume-button gesture (enabled flag + press count).

Both mappers are straight field-for-field pass-throughs. The storage-side encoding tricks that make proto3 defaults behave (inverted `*_disabled` booleans, the offset-by-one reload hour) are undone one layer down in `data-preferences-impl`, so everything at this level already speaks plain nullable values.

### Screen State

`ScreenStateResourceMapper` bidirectionally maps `ScreenStateResource` and `ScreenStateModel`, including the `DarkOverlay` state (`@since 1.2.0`) — a plain dark overlay used as a stand-in for powering the screen off on devices such as Android TV where the panel cannot be turned off.
