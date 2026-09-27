# domain-usecase-api

This module defines the Use Case (Interactor) interfaces for the application. Each interface represents a single, atomic business operation or user action, following the Clean Architecture principle.

## Features

- **Business Operations**: Defines interfaces for all actions the user or system can perform (e.g., `SetThemeUseCase`, `GetDashboardUseCase`).
- **Standardized Results**: Uses `Result<T>` for all return types to ensure consistent error handling across the domain layer.
- **Optional Alias**: Provides the `Optional` typealias (`Result<Unit>`) for operations that return no specific value but can still fail.
- **Camera Use Cases** (`.camera`, `@since 1.4.0`): `GetCameraSourceUseCase`, `SetCameraSourceUseCase`, and `ObserveCameraSourceUseCase` expose the user's camera-source preference (`Auto` / `Front` / `Rear` / `External`) that overrides automatic camera selection for motion detection and MJPEG streaming.
- **MQTT form-factor reporting** (`@since 1.2.0`): `MqttConnectUseCase.invoke(model: String)` requires a `model` parameter (`"tv"` or `"tablet"`), supplied by the caller from the current form-factor and reported to Home Assistant discovery.
- **Interaction Use Cases** (`.configuration`, `@since 2.2.0`): `GetInteractionUseCase`, `SetInteractionUseCase`, and `ObserveInteractionUseCase` read, persist, and observe the inactivity-reset timeout and volume-gesture settings.
- **Resilience Use Cases** (`.configuration`, `@since 2.2.0`): `GetResilienceUseCase`, `SetResilienceUseCase`, and `ObserveResilienceUseCase` read, persist, and observe the unattended-operation safeguards — crash auto-relaunch, scheduled reload, memory recovery, connection monitoring, and Wi-Fi lock — that back the "T2 Survivability" tier.
- **MQTT Discovery Cleanup** (`.mqtt`, `@since 2.1.0`): `MqttPurgeDiscoveryUseCase` retracts every Home Assistant discovery entity the device has registered; call it before `MqttDisconnectUseCase` when the user turns MQTT off, since discovery configs are retained and won't disappear on their own.
- **Companion Telemetry** (`.mqtt`, `@since 2.1.0`): `MqttSendCompanionTelemetryUseCase` publishes the periodically-sampled uptime, app version, IP address, and RAM usage in a single call.
- **Dashboard Reachability** (`.mqtt`, `@since 2.2.0`): `MqttSendDashboardStateUseCase` publishes whether the dashboard backend is reachable, on its own `dashboard` binary sensor kept separate from device availability.
- **Remote MQTT Commands** (`.mqtt`): `ObserveMqttClearCacheCommandUseCase` (`@since 2.1.0`) observes Home Assistant `clear_cache` button presses; `ObserveMqttRemoteCommandUseCase` (`@since 2.2.0`) decodes the shared `{clientId}/command/set` topic into a `RemoteCommandModel` (`navigate`, `reload`, `back`, etc.) for scripted WebView control.

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
