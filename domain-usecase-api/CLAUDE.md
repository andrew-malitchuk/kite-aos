# Module: domain-usecase-api

## Overview
This module defines the Use Case (Interactor) interfaces for the application. Each interface represents a single, atomic business operation or user action.

## Responsibilities
*   **Business Operations**: Defines interfaces for all actions the user or system can perform (e.g., `SetThemeUseCase`, `GetDashboardUseCase`).
*   **Input/Output Contracts**: Defines the boundaries of business logic execution and error representation.

## Architecture
This module follows the Clean Architecture principle where each Use Case is a specific entry point to the domain layer. It depends on `domain-core` for data models and `common-core` for shared utilities.

### Key Components
*   **Application Use Cases** (`.application`): `SaveApplicationUseCase`, `GetApplicationsUseCase`, `LoadApplicationsUseCase`, etc.
*   **MQTT Use Cases** (`.mqtt`): `MqttConnectUseCase`, `SetMqttConfigurationUseCase`, `MqttSendMotionUseCase`, etc. `MqttConnectUseCase.invoke(model: String)` requires a `model` parameter (`"tv"` or `"tablet"`; `@since 1.2.0`), supplied by the caller from the current form-factor (via `AppConfig.isTv`) and reported to Home Assistant discovery.
*   **Configuration Use Cases** (`.configuration`): `SetThemeUseCase`, `SetDashboardUseCase`, `SetOnboardingStatusUseCase`, `SetApplicationLanguageUseCase`, etc.
*   **Camera Use Cases** (`.camera`) (`@since 1.4.0`): `GetCameraSourceUseCase`, `SetCameraSourceUseCase`, `ObserveCameraSourceUseCase` — read, persist, and observe the user's `CameraSourceModel` preference (`Auto` / `Front` / `Rear` / `External`) that overrides automatic camera selection for motion detection and MJPEG streaming.
*   **Interaction & Resilience Use Cases** (`.configuration`) (`@since 2.2.0`): `GetInteractionUseCase`/`SetInteractionUseCase`/`ObserveInteractionUseCase` expose the `InteractionModel` (inactivity-reset timeout, volume-gesture). `GetResilienceUseCase`/`SetResilienceUseCase`/`ObserveResilienceUseCase` expose the `ResilienceModel` (crash auto-relaunch, scheduled reload, memory recovery, connection monitoring, Wi-Fi lock) that backs the "T2 Survivability" tier. Both `Get*` use cases substitute a default model when nothing is persisted; both `Set*` use cases accept `null` to reset to defaults.
*   **Device Use Cases** (`.device`): `SetDockPositionUseCase`, `SetMoveDetectorUseCase`, `ObserveMoveDetectorMotionUseCase`, etc.
*   **MQTT Discovery & Telemetry Use Cases** (`.mqtt`) (`@since 2.1.0`): `MqttPurgeDiscoveryUseCase` retracts every registered Home Assistant discovery entity — invoke before `MqttDisconnectUseCase`, since discovery configs are retained messages that otherwise survive disconnect. `MqttSendCompanionTelemetryUseCase` publishes a `CompanionTelemetryModel` sample (uptime, app version, IP address, RAM usage) in a single call, since those values are sampled periodically rather than event-driven.
*   **MQTT Remote Command Use Cases** (`.mqtt`): `ObserveMqttClearCacheCommandUseCase` (`@since 2.1.0`) emits on every Home Assistant `clear_cache` button press. `ObserveMqttRemoteCommandUseCase` (`@since 2.2.0`) decodes the shared `{clientId}/command/set` topic into a `RemoteCommandModel`, dropping malformed payloads silently so a typo in a Home Assistant automation cannot take the command channel down. `MqttSendDashboardStateUseCase` (`@since 2.2.0`) publishes dashboard-backend reachability on its own `dashboard` binary sensor, intentionally separate from device availability.

### Error Handling
Use cases return `Result<T>` or `Optional` (`Result<Unit>`). Success contains the requested data, while failure contains a `domain.core.source.monad.Failure` entity.

## Dependencies
*   **`domain-core`**: Provides domain models.
*   **`common-core`**: Base infrastructure.
