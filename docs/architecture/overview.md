# Architecture Overview

Kite AOS follows **Clean Architecture** with a strict layered dependency model across 40+ Gradle modules.

## Dependency Flow

Dependencies only point downward — upper layers depend on abstractions, never on implementations.

```
presentation-feature-*  →  domain-usecase-api  →  domain-repository-api
presentation-core-*         domain-usecase-impl     data-repository-impl
                            domain-core             data-*-api / data-*-impl
                                                    common-core / data-core
```

## Module Naming Convention

| Pattern | Description |
|---------|-------------|
| `{layer}-{sublayer}-api` | Interfaces and contracts — pure Kotlin where possible |
| `{layer}-{sublayer}-impl` | Implementations — Android-aware |
| `{layer}-core` | Shared abstractions for that layer |
| `presentation-feature-{name}` | Independent feature screen |

## Layers

### Presentation
- **`presentation-feature-*`** — Feature screens (Compose UI + ViewModel).
- **`presentation-core-application`** — App entry point, Koin composition root.
- **`presentation-core-platform`** — Motion detection, MQTT services, hardware control.
- **`presentation-core-styling`** — Design system, theme, typography, tokens.
- **`presentation-core-ui`** — Reusable atomic Compose components.
- **`presentation-core-navigation-*`** — Navigation contracts and Navigation 3 implementation.

### Domain
- **`domain-core`** — Pure Kotlin models (`Failure`, marker types). No Android dependencies.
- **`domain-repository-api`** — Repository interfaces consumed by use cases.
- **`domain-usecase-api`** — Use case interfaces consumed by ViewModels.
- **`domain-usecase-impl`** — Concrete use case implementations with `resultLauncher` error wrapping.

### Data
- **`data-repository-impl`** — Orchestrates all data sources into domain models.
- **`data-database-*`** — Room persistence (contracts + implementation).
- **`data-preferences-*`** — Proto DataStore persistence (contracts + implementation).
- **`data-mqtt-*`** — MQTT telemetry (contracts + implementation).
- **`data-platform-*`** — Android platform data sources (battery, installed apps).
- **`data-core`** — `Resource` marker interface shared across data modules.

### Infrastructure
- **`common-core`** — Coroutine utilities and mapping abstractions.
- **`build-logic`** — Convention plugins for Gradle build configuration.

## Convention Plugins

Modules don't configure builds directly. They apply convention plugins from `build-logic/`:

| Plugin ID | Purpose |
|-----------|---------|
| `dev.yahk.convention.application` | Android app (Compose, signing, buildConfig) |
| `dev.yahk.convention.feature` | Android library with Compose + serialization |
| `dev.yahk.convention.library` | Pure Kotlin/JVM library (`ExplicitApiMode.Strict`) |
| `dev.yahk.convention.di` | Koin + KSP for Kotlin modules |
| `dev.yahk.convention.di.android` | Koin + KSP for Android modules |
| `dev.yahk.convention.quality` | Detekt + Ktlint setup |

!!! note "ExplicitApiMode"
    Pure Kotlin library modules (`domain-core`, `common-core`, `data-core`, `domain-repository-api`, `domain-usecase-api`) enforce `ExplicitApiMode.Strict` — all public declarations require explicit visibility modifiers and return types.

## Error Handling

Errors are modeled as a `Failure` sealed class in `domain-core`:

```kotlin
sealed class Failure {
    sealed class Technical : Failure() {
        class Database(cause: Throwable) : Technical()
        class Platform(cause: Throwable) : Technical()
        class Network(cause: Throwable) : Technical()
        class Preference(cause: Throwable) : Technical()
    }
    sealed class Logic : Failure() {
        object NotFound : Logic()
        class Business(message: String) : Logic()
    }
}
```

Use cases return `Result<T>`. The `resultLauncher` utility in `domain-usecase-impl` wraps suspend calls and maps exceptions to the appropriate `Failure` subtype:

```kotlin
override suspend fun invoke(): Result<ThemeModel> = resultLauncher(
    errorMapper = Failure.Technical::Preference
) {
    repository.getTheme() ?: throw Failure.Logic.NotFound
}
```

## Key Technologies

| Concern | Library |
|---------|---------|
| UI | Jetpack Compose + Material 3 |
| State management | Orbit MVI 11.0.0 |
| Dependency injection | Koin 4.1.1 + koin-annotations 2.3.1 (KSP) |
| Navigation | androidx.navigation3 1.0.0 |
| Database | Room 2.8.4 |
| Preferences | Proto DataStore + Protobuf |
| MQTT | kmqtt-client-jvm 1.0.0 |
| Functional | Arrow Core 1.2.4 |
| Camera / Motion | CameraX |
