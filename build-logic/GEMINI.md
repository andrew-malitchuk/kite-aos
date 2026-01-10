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
