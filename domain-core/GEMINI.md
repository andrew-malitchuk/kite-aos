# Module: domain-core

## Overview
This module serves as the central core of the Domain layer. It contains the fundamental building blocks used across all domain-related modules, including base model interfaces, central data models (entities), and core domain logic such as standardized error handling.

## Responsibilities
*   **Domain Models**: Defines the core data entities used by the business logic, such as `ApplicationModel`, `DashboardModel`, `ThemeModel`, `MoveDetectorModel`, and `MqttModel`.
*   **Error Handling (Monads)**: Provides the `Failure` sealed class hierarchy to standardize how errors (Technical vs. Logic) are represented and handled within the domain layer.
*   **Base Definitions**: Defines the `Model` interface, which acts as a marker for all domain models.

## Architecture
This is a pure Kotlin module that sits at the center of the architecture. It does not depend on any other layers (Data or Presentation), ensuring that the business logic remains decoupled from infrastructure and UI concerns.

### Key Components
*   **`domain.core.core.monad.Failure`**: A structured way to represent errors. 
    *   `Technical`: For infrastructure issues (Database, Network, Platform).
    *   `Logic`: For business rules or "Not Found" scenarios.
*   **`domain.core.source.model.*`**: Data classes representing the state of various components like Dock position, Onboarding status, and Application info.

## Dependencies
*   **`common-core`**: Provides shared utilities and base infrastructure needed by the domain core.
