# Module: domain-usecase-impl

## Overview
This module provides the concrete implementations of the Use Case interfaces defined in `domain-usecase-api`. It contains the core business logic of the application.

## Responsibilities
*   **Logic Execution**: Implements the business rules by orchestrating calls to repositories and applying domain-specific transformations.
*   **Error Transformation**: Maps technical and platform exceptions into standardized domain `Failure` entities.
*   **Dependency Injection**: Annotated with Koin annotations for automatic binding to API interfaces.

## Architecture
The module implements the `domain-usecase-api` contracts. It interacts with `domain-repository-api` to fetch or persist data. It is decoupled from specific data source details via repository interfaces.

### Key Implementations
Implementations are organized into sub-packages mirroring the API:
*   **`application`**: `LoadApplicationsUseCaseImpl`, `SaveApplicationUseCaseImpl`, etc.
*   **`configuration`**: `SetThemeUseCaseImpl`, `GetOnboardingStatusUseCaseImpl`, etc.
*   **`camera`** (`@since 1.4.0`): `GetCameraSourceUseCaseImpl`, `SetCameraSourceUseCaseImpl`, `ObserveCameraSourceUseCaseImpl` — back the user's `CameraSourceModel` preference used for motion detection and MJPEG streaming.
*   **`device`**: `SetMoveDetectorUseCaseImpl`, `ObserveMoveDetectorMotionUseCaseImpl`, etc.
*   **`configuration`** (`@since 2.2.0`): `GetInteractionUseCaseImpl`/`SetInteractionUseCaseImpl`/`ObserveInteractionUseCaseImpl` and `GetResilienceUseCaseImpl`/`SetResilienceUseCaseImpl`/`ObserveResilienceUseCaseImpl` — thin `ConfigureRepository` delegates for the interaction and unattended-operation-safeguard ("T2 Survivability") settings. Each `Get*Impl` substitutes a default model (`InteractionModel()` / `ResilienceModel()`) when nothing has been persisted; each `Set*Impl` forwards `null` straight through to reset to defaults.
*   **`mqtt`**: `MqttConnectUseCaseImpl` (forwards the `model` form-factor argument `"tv"`/`"tablet"` to the repository for Home Assistant discovery; `@since 1.2.0`), `MqttSendMotionUseCaseImpl`, etc. `@since 2.1.0` adds `MqttPurgeDiscoveryUseCaseImpl` (retracts registered discovery entities via `MqttRepository.purgeDiscovery()`), `MqttSendCompanionTelemetryUseCaseImpl` (forwards a `CompanionTelemetryModel` sample), and `ObserveMqttClearCacheCommandUseCaseImpl` (filters `MqttRepository.observeCommands()` for topics ending `_clear_cache/clear_cache/press` and maps to `Unit`). `@since 2.2.0` adds `MqttSendDashboardStateUseCaseImpl` (forwards dashboard-backend reachability) and `ObserveMqttRemoteCommandUseCaseImpl`, which filters the same shared command flow for the `/command/set` suffix and decodes each JSON payload into a `RemoteCommandModel` via `kotlinx.serialization.json.Json { ignoreUnknownKeys = true }`, dropping anything that fails to decode.

### Utilities
*   **`resultLauncher`**: An internal utility that wraps suspendable blocks, handles `CancellationException` (for coroutine safety), and maps other errors using a provided mapper.

## Dependencies
*   **`domain-usecase-api`**: Provides the interfaces to implement.
*   **`domain-repository-api`**: Provides repositories for data access.
*   **`domain-core`**: Provides domain models and failure types.
