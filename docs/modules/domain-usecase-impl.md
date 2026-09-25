# domain-usecase-impl

This module provides the concrete implementations of the Use Case interfaces defined in `domain-usecase-api`. It contains the actual business logic of the application, orchestrating calls to repositories and applying domain-specific rules.

## Features

- **Logic Execution**: Implements business rules by coordinating repository calls.
- **Unified Error Handling**: Utilizes `resultLauncher` to consistently wrap operations in `Result<T>` and map exceptions to domain `Failure` types.
- **Automated DI**: Uses Koin annotations for automatic dependency discovery and injection.
- **Survivability & Interaction Backing** (`@since 2.2.0`): `GetInteractionUseCaseImpl`/`SetInteractionUseCaseImpl`/`ObserveInteractionUseCaseImpl` and `GetResilienceUseCaseImpl`/`SetResilienceUseCaseImpl`/`ObserveResilienceUseCaseImpl` delegate to `ConfigureRepository`, substituting a default model on read and accepting `null` to reset to defaults on write.
- **MQTT Discovery, Telemetry & Remote Commands** (`.mqtt`): `MqttPurgeDiscoveryUseCaseImpl` and `MqttSendCompanionTelemetryUseCaseImpl` (`@since 2.1.0`), plus `MqttSendDashboardStateUseCaseImpl` (`@since 2.2.0`), forward to `MqttRepository`. `ObserveMqttClearCacheCommandUseCaseImpl` (`@since 2.1.0`) and `ObserveMqttRemoteCommandUseCaseImpl` (`@since 2.2.0`) filter the repository's shared inbound-command `Flow` by topic suffix, the latter decoding the JSON payload into a `RemoteCommandModel` via `kotlinx.serialization.json`.

## Implementation Details

### Result Launcher
The `resultLauncher` is a utility used in implementations to catch platform-specific or technical exceptions and convert them into structured domain failures.

```kotlin
override suspend fun invoke(): Result<ThemeModel> = resultLauncher(
    errorMapper = Failure.Technical::Preference
) {
    repository.getTheme() ?: throw Failure.Logic.NotFound
}
```
