# domain-usecase-api

This module defines the Use Case (Interactor) interfaces for the application. Each interface represents a single, atomic business operation or user action, following the Clean Architecture principle.

## Features

- **Business Operations**: Defines interfaces for all actions the user or system can perform (e.g., `SetThemeUseCase`, `GetDashboardUseCase`).
- **Standardized Results**: Uses `Result<T>` for all return types to ensure consistent error handling across the domain layer.
- **Optional Alias**: Provides the `Optional` typealias (`Result<Unit>`) for operations that return no specific value but can still fail.
- **Camera Use Cases** (`.camera`, `@since 1.4.0`): `GetCameraSourceUseCase`, `SetCameraSourceUseCase`, and `ObserveCameraSourceUseCase` expose the user's camera-source preference (`Auto` / `Front` / `Rear` / `External`) that overrides automatic camera selection for motion detection and MJPEG streaming.
- **MQTT form-factor reporting** (`@since 1.2.0`): `MqttConnectUseCase.invoke(model: String)` requires a `model` parameter (`"tv"` or `"tablet"`), supplied by the caller from the current form-factor and reported to Home Assistant discovery.

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
