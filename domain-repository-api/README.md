# domain-repository-api

This module defines the repository interfaces for the Domain layer. These interfaces act as contracts for data access, following the Repository Pattern to decouple business logic from data sources.

## Features

- **Data Contracts**: Defines abstract repository interfaces (e.g., `ApplicationRepository`, `ConfigureRepository`, `MqttRepository`) that describe *what* data is needed by the domain.
- **Abstraction**: Ensures that Use Cases only interact with abstractions, allowing the underlying data implementation (Network, Database, Preferences, MQTT) to change without affecting business logic.

## Usage

Feature modules and Use Cases should depend on this module to interact with data. The implementation of these interfaces is provided by the `data-repository-impl` module.

```kotlin
// In a Use Case
class GetThemeUseCase(private val repository: ConfigureRepository) {
    suspend operator fun invoke() = repository.getTheme()
}
```
