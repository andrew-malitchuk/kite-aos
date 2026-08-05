# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Summary

Kite AOS is an Android kiosk application for smart home dashboards (primarily Home Assistant). It transforms tablets into locked-down, motion-aware web-view interfaces with MQTT telemetry. Built with Jetpack Compose, Orbit MVI, Koin DI, and Clean Architecture across 40+ Gradle modules.

## Build Commands

```bash
./gradlew build                    # Full build
./gradlew assembleDebug            # Debug APK (all flavors)
./gradlew :presentation-core-application:installDebug  # Install on device

# Flavor-specific builds (distribution × formfactor: gms/foss × mobile/tv)
./gradlew :presentation-core-application:installFossMobileDebug  # phone/tablet, GeckoView
./gradlew :presentation-core-application:installFossTvDebug      # Android TV, GeckoView
./gradlew :presentation-core-application:assembleGmsTvRelease    # Android TV, system WebView + Firebase

# Code quality
./gradlew detekt                   # Static analysis (maxIssues: 0, strict)
./gradlew ktlintCheck              # Style check
./gradlew ktlintFormat             # Auto-format
./gradlew check                    # Runs detekt + ktlintCheck

# Single module build
./gradlew :module-name:build
```

No test suites exist currently. Quality enforcement is via Detekt and Ktlint.

## Architecture

### Layer Structure (dependency flows top → bottom)

```
presentation-feature-*  →  domain-usecase-api  →  domain-repository-api
presentation-core-*         domain-usecase-impl     data-repository-impl
                            domain-core             data-*-api / data-*-impl
                                                    common-core / data-core
```

### Module Naming Convention

- **`{layer}-{sublayer}-api`** — Interfaces/contracts (pure Kotlin where possible)
- **`{layer}-{sublayer}-impl`** — Implementations (Android-aware)
- **`{layer}-core`** — Shared abstractions for that layer
- **`presentation-feature-{name}`** — Independent feature screens

### Convention Plugins (`build-logic/convention/`)

Modules don't configure builds directly. Instead they apply convention plugins:

| Plugin ID | Purpose |
|---|---|
| `dev.yahk.convention.application` | Android app module (compose, signing, buildConfig) |
| `dev.yahk.convention.feature` | Android library with Compose + serialization |
| `dev.yahk.convention.library` | Pure Kotlin/JVM library (**ExplicitApiMode.Strict**) |
| `dev.yahk.convention.di` | Koin + KSP for Kotlin modules |
| `dev.yahk.convention.di.android` | Koin + KSP for Android modules |
| `dev.yahk.convention.quality` | Detekt + Ktlint setup |

**Key**: Pure Kotlin library modules (`domain-core`, `common-core`, `data-core`, `domain-repository-api`, `domain-usecase-api`) use `ExplicitApiMode.Strict` — all public declarations need explicit visibility modifiers and return types.

### Product Flavors (two dimensions)

The build has **two** flavor dimensions (configured in the convention plugins, not per-module):

| Dimension | Flavors | Meaning |
|---|---|---|
| `distribution` | `foss` / `gms` | `foss` = GeckoView engine, no GMS/Firebase (F-Droid friendly); `gms` = system Android WebView + Firebase |
| `formfactor` | `mobile` / `tv` | `mobile` = phone/tablet (touch); `tv` = Android TV / leanback (D-pad remote) |

Full matrix, no `variantFilter` prunes anything → **4 flavors** (`gmsMobile`, `fossMobile`, `gmsTv`, `fossTv`) × debug/release = 8 variants.

- The application module exposes `BuildConfig.IS_TV` (`false` for `mobile`, `true` for `tv`). Feature/core libraries **cannot** read the app `BuildConfig`, so runtime form-factor detection is centralised in the **`AppConfig`** abstraction (`presentation-core-platform`); `YahkApplication` seeds `AppConfig.buildFlagIsTv` from `BuildConfig.IS_TV`.
- Libraries only mirror the `formfactor` dimension (no `IS_TV` field) so variant-aware dependency matching resolves and `src/tv/` source-sets compile into `tv` variants. The app's extra `distribution` dimension auto-falls-back for libraries — no `missingDimensionStrategy` needed.
- Mobile and TV share one `applicationId` (`dev.kite.aos`). TV gets `versionCode + TV_VERSION_CODE_OFFSET` (`1_000_000`) so Play multi-APK delivery can serve them distinctly.
- **TV-specific code lives in `src/tv/` source-sets** of `presentation-*` modules only. All `domain-*` and `data-*` modules are shared/unchanged across form-factors. See [docs/android-tv.md](docs/android-tv.md).

### MVI Pattern (Orbit)

Every feature module follows the same structure:

```
presentation-feature-{name}/
  source/{name}/
    {Name}Screen.kt       — Navigation entry, collectAsState/collectSideEffect
    {Name}Content.kt      — Stateless @Composable receiving state + callbacks
    {Name}ViewModel.kt    — ContainerHost<State, SideEffect>, @KoinViewModel
    {Name}Contract.kt     — State data class + SideEffect sealed class
    {Name}Intent.kt       — Sealed class of user actions
  di/
    PresentationFeature{Name}Module.kt — @Module @ComponentScan
```

ViewModels use `intent { reduce { ... } }` for state and `postSideEffect()` for one-shot UI events.

### Dependency Injection (Koin with KSP annotations)

- Composition root: `presentation-core-application/di/AppModule.kt` — includes all layer modules
- Use `@Single(binds = [Interface::class])` for implementations
- Use `@KoinViewModel` for ViewModels
- Each impl module has a `@Module @ComponentScan` class that KSP processes

### Error Handling

- `Failure` sealed class in `domain-core` (subtypes: `Technical.Database`, `Technical.Platform`, `Technical.Network`, `Technical.Preference`, `Logic.NotFound`, `Logic.Business`)
- UseCases return `Result<T>` (Kotlin stdlib)
- `resultLauncher()` in `domain-usecase-impl` wraps suspend calls and maps exceptions to Failures

### Navigation

- `Destination` sealed class in `presentation-core-navigation-api` — all routes are `@Serializable` data objects
- Uses `androidx.navigation3` (v1.0.0)
- `AppNavigator` abstraction with `NavCompositionLocal` for injection

### Key Services

- **MotionService** — Foreground service using CameraX for presence detection (luma analysis at 176x144). Controls screen wake/dim/lock based on motion. On the `tv` flavor it sources frames from a USB webcam via `Camera2ExternalMotionSource` → `UvcMotionSource` (libausbc) fallback, or presence pulses from `MqttMotionSource` when no camera exists.
- **MqttService** — Foreground service for MQTT telemetry (battery, motion events, Home Assistant discovery). Publishes `device_class = tv` on the `tv` flavor.

## Code Style

- Kotlin 2.3.0, Java 21, Android API 26–36
- 120 character line limit, 4-space indent, trailing commas allowed
- Detekt: strict (`maxIssues: 0`), but relaxed thresholds for `@Composable` functions (MagicNumber, LongMethod, LongParameterList, CyclomaticComplexity all have `ignoreAnnotated: ['Composable']`)
- Ktlint: android code style, warnings only (doesn't fail build)
- Detekt config at `config/detekt/detekt.yml`

## Module Registry

| Category     | Module                             | Role                                                   |
|:-------------|:-----------------------------------|:-------------------------------------------------------|
| **App**      | `presentation-core-application`    | Composition root and Android entry point.              |
| **Feature**  | `presentation-feature-main`        | The primary kiosk dashboard and control drawer.        |
| **Feature**  | `presentation-feature-settings`    | Comprehensive system and user configuration.           |
| **Feature**  | `presentation-feature-onboarding`  | Guided first-run setup and permission wizard.          |
| **Feature**  | `presentation-feature-host`        | Single-activity host and immersive mode controller.    |
| **Feature**  | `presentation-feature-about`       | Project information and social connectivity.           |
| **Feature**  | `presentation-feature-application` | Managed application launcher selection.                |
| **UI Core**  | `presentation-core-styling`        | Central design system and theme management.            |
| **UI Core**  | `presentation-core-ui`             | Reusable atomic components and design tokens.          |
| **Platform** | `presentation-core-platform`       | Motion detection, MQTT services, and hardware control. |
| **Data**     | `data-repository-impl`             | Orchestration of all data sources into domain models.  |
| **Domain**   | `domain-usecase-impl`              | Concrete implementation of application business rules. |

Each module contains its own `CLAUDE.md` with deep-dive technical documentation.

## Key Technologies

- **UI**: Jetpack Compose + Material 3, custom Squircle shape system
- **State**: Orbit MVI 11.0.0
- **DI**: Koin 4.1.1 + koin-annotations 2.3.1 (KSP)
- **Database**: Room 2.8.4
- **Preferences**: Proto DataStore with Protobuf serialization
- **MQTT**: kmqtt-client-jvm 1.0.0
- **Functional**: Arrow Core 1.2.4
- **Navigation**: androidx.navigation3 1.0.0
