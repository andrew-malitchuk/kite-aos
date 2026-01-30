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
*   **`device`**: `SetMoveDetectorUseCaseImpl`, `ObserveMoveDetectorMotionUseCaseImpl`, etc.
*   **`mqtt`**: `MqttConnectUseCaseImpl`, `MqttSendMotionUseCaseImpl`, etc.

### Utilities
*   **`resultLauncher`**: A internal utility that wraps suspendable blocks, handles `CancellationException` (for coroutine safety), and maps other errors using a provided mapper.

## Dependencies
*   **`domain-usecase-api`**: Provides the interfaces to implement.
*   **`domain-repository-api`**: Provides repositories for data access.
*   **`domain-core`**: Provides domain models and failure types.
