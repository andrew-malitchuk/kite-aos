# Module: build-logic

## Overview
This module is a standalone Gradle project dedicated to defining the build logic and conventions used throughout the entire repository. It uses the Gradle **Kotlin DSL** and **Convention Plugins** to provide a single source of truth for build configurations, reducing boilerplate and ensuring consistency across all modules.

## Responsibilities
*   **Standardization**: Enforces consistent Android and Kotlin settings (SDK versions, Java compatibility, lint options) across all modules.
*   **Dependency Management**: Abstracts dependency addition using the Version Catalog (`libs.versions.toml`) through custom extension functions.
*   **Feature Modularization**: Provides specialized plugins for different types of modules (Applications, Features, Pure Kotlin Libraries).
*   **DI Configuration**: Centralizes the setup for Koin (Dependency Injection) and KSP (Kotlin Symbol Processing).

## Architecture
The logic is contained within the `convention` subproject:
*   **`core.ext`**: Contains extension functions for Gradle types (`Project`, `DependencyHandler`, `String`, `Lint`, `Signing`) to provide a cleaner DSL. It includes centralized logic for:
    *   `configureAndroidBase`: Standardizes SDK versions and Java compatibility.
    *   `configureKotlinBase`: Applies `ExplicitApiMode.Strict` project-wide.
    *   `configureSigning`: Manages debug and release signing configurations via property files.
*   **`source.conventionplugin`**: Contains the concrete implementations of `Plugin<Project>`, grouped by their domain (Android, DI, Kotlin).

## Key Plugins

| Plugin ID | Class Name | Purpose |
| :--- | :--- | :--- |
| `dev.yahk.convention.application` | `AndroidApplicationConventionPlugin` | Configures the main Android application module, including signing, build features, and base Compose dependencies. |
| `dev.yahk.convention.feature` | `AndroidFeatureConventionPlugin` | Configures Android library modules used for UI features, enabling Compose, serialization, and KSP. |
| `dev.yahk.convention.di.android` | `DiAndroidConventionPlugin` | Sets up Koin for Android modules, including Compose and ViewModel support. |
| `dev.yahk.convention.di` | `DiConventionPlugin` | Sets up Koin for pure Kotlin/JVM modules. |
| `dev.yahk.convention.library` | `KotlinLibraryConventionPlugin` | Configures pure Kotlin/JVM library modules with strict API mode. |

## Flavor Dimensions

The application declares **two** flavor dimensions, so variants are the full cross-product `{distribution} x {formfactor} x {buildType}` — no `variantFilter` prunes anything:

*   **`distribution`** — packaging/engine axis:
    *   `foss` — GeckoView (`minSdk = 26`, imposed by GeckoView 147+'s manifest constraint).
    *   `gms` — system Android WebView + Firebase.
*   **`formfactor`** — device axis (new, for Android TV support):
    *   `mobile` — phones/tablets; sets `buildConfigField boolean IS_TV false`, keeps the base `versionCode`.
    *   `tv` — Android TV; sets `IS_TV true` and offsets `versionCode = base + TV_VERSION_CODE_OFFSET` (`1_000_000`).

This yields four application flavors — `gmsMobile`, `fossMobile`, `gmsTv`, `fossTv` — each with `debug` and `release` build types.

### Why the TV versionCode offset

Mobile and TV share a single `applicationId` (`dev.kite.aos`), so Play multi-APK delivery needs distinct `versionCode`s to serve the right APK per device class. Mobile keeps the base `versionCode` for release continuity, while TV is offset into a separate range that still tracks the base as it increments. `TV_VERSION_CODE_OFFSET` (`1_000_000`) sits far above any realistic base value, so the mobile and TV ranges never collide.

### Feature/core libraries mirror `formfactor`

`AndroidFeatureConventionPlugin` gives every feature/core library the same `formfactor` dimension (`mobile` + `tv`, no `buildConfigField`). Mirroring the dimension lets variant-aware dependency matching resolve automatically — the app and its libraries share identical flavor names — and it is what enables `src/tv` (and `src/mobile`) source-sets inside libraries. Libraries do **not** declare `IS_TV`: the runtime flag lives on the app's `BuildConfig` and is surfaced to libraries through the `AppConfig` abstraction. The app's extra `distribution` dimension auto-falls-back for libraries, so **no `missingDimensionStrategy` is required**.

## DSL Extensions
The module provides several helpers to make build scripts more readable:
*   `implementDependency(libs, "alias")`: Adds a library from the catalog to `implementation`.
*   `implementKsp(libs, "alias")`: Adds a KSP processor.
*   `app { ... }`: Wrapper for `ApplicationExtension`.
*   `lib { ... }`: Wrapper for `LibraryExtension`.
*   `jvm { ... }`: Wrapper for `JavaPluginExtension`.
*   `configureSigning(extension)`: Internal helper to apply signing logic.

## Dependencies
*   **Android Gradle Plugin**: Core building logic.
*   **Kotlin Gradle Plugin**: Kotlin language support.
*   **KSP**: For annotation processing.
*   **Compose Compiler**: For Jetpack Compose support.
