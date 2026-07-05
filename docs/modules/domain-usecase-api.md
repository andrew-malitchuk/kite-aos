# domain-usecase-api

This module defines the Use Case (Interactor) interfaces for the application. Each interface represents a single, atomic business operation or user action, following the Clean Architecture principle.

## Features

- **Business Operations**: Defines interfaces for all actions the user or system can perform (e.g., `SetThemeUseCase`, `GetDashboardUseCase`).
- **Standardized Results**: Uses `Result<T>` for all return types to ensure consistent error handling across the domain layer.
- **Optional Alias**: Provides the `Optional` typealias (`Result<Unit>`) for operations that return no specific value but can still fail.

## Usage

Feature modules (ViewModels) should depend on this module to execute business logic. Use cases are injected as interfaces to maintain decoupling.

```kotlin
class MyViewModel(private val getThemeUseCase: GetThemeUseCase) {
    fun loadTheme() {
        viewModelScope.launch {
            val result = getThemeUseCase()
            // Handle result
        }
    }
}
```
