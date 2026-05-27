# Module: domain-repository-api

## Overview
This module defines the repository interfaces for the Domain layer. These interfaces act as contracts for data access, following the Repository Pattern to decouple business logic from data sources.

## Responsibilities
*   **Data Contracts**: Defines abstract repository interfaces (e.g., `ApplicationRepository`, `ConfigureRepository`) that describe *what* data is needed by the domain, without specifying *how* it is retrieved or stored.
*   **Abstraction**: Ensures that Use Cases only interact with abstractions, allowing the underlying data implementation (Network, Database, Preferences) to change without affecting business logic.

## Architecture
This module depends on `domain-core` for domain models and failures. It is purely an API module containing interfaces.

### Key Interfaces
*   **`ApplicationRepository`**: Contract for managing applications. Uses `loadApplications()` for database retrieval and `getApplications()` for system-installed apps.
*   **`ConfigureRepository`**: Contract for managing system and user configurations (Theme, Dashboard, Dock, Onboarding).
*   **`MqttRepository`**: Contract for MQTT telemetry operations and configuration.

## Dependencies
*   **`domain-core`**: Provides the domain models used in repository method signatures.
*   **`common-core`**: Shared utilities and base classes.
