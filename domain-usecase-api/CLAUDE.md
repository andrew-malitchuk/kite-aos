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
*   **Device Use Cases** (`.device`): `SetDockPositionUseCase`, `SetMoveDetectorUseCase`, `ObserveMoveDetectorMotionUseCase`, etc.

### Error Handling
Use cases return `Result<T>` or `Optional` (`Result<Unit>`). Success contains the requested data, while failure contains a `domain.core.source.monad.Failure` entity.

## Dependencies
*   **`domain-core`**: Provides domain models.
*   **`common-core`**: Base infrastructure.
