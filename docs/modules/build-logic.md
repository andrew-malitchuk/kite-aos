# build-logic

Shared build configuration and convention plugins for the project.

## Overview
This directory contains the Gradle logic that powers the builds for all modules in this repository. By using convention plugins, we avoid duplicating build scripts and ensure that every module follows the same standards for SDK versions, compiler flags, and dependency management.

## Available Plugins

### Android
- `dev.yahk.convention.application`: Apply to the `:app` module.
- `dev.yahk.convention.feature`: Apply to any Android library module that represents a UI feature.

### Dependency Injection (Koin)
- `dev.yahk.convention.di.android`: Sets up Koin for Android modules.
- `dev.yahk.convention.di`: Sets up Koin for pure Kotlin modules.

### Kotlin
- `dev.yahk.convention.library`: Apply to pure Kotlin/JVM library modules.

## How to use
In your module's `build.gradle.kts` file, apply the required convention plugins. Most UI modules will need both a base plugin and a DI plugin:

```kotlin
plugins {
    // Base configuration (choose one)
    id("dev.yahk.convention.application") // For the app module
    // OR
    id("dev.yahk.convention.feature")     // For Android feature modules
    // OR
    id("dev.yahk.convention.library")     // For pure Kotlin JVM modules

    // Dependency Injection (choose one)
    id("dev.yahk.convention.di.android")  // For Android modules
    // OR
    id("dev.yahk.convention.di")          // For pure Kotlin modules
}

android {
    namespace = "dev.yahk.feature.example"
}

dependencies {
    // Use the simplified helpers to add dependencies from the version catalog
    implementDependency(libs, "koin.core")
    implementKsp(libs, "koin.ksp.compiler")
}
```

## Internal Architecture
The build logic is designed to be highly reusable:
- **`Project.configureAndroidBase`**: Centralizes SDK versions (compile, min, target) and Java compatibility.
- **`Project.configureKotlinBase`**: Enforces strict explicit API mode, ensuring all public declarations are explicitly typed and scoped.
- **`Project.configureSigning`**: Automatically handles secure signing using local property files.

## Flavor Dimensions
The application module (`dev.yahk.convention.application`) declares two flavor dimensions, and no `variantFilter` prunes the matrix, so all combinations are built:

- **`distribution`**: `foss` (GeckoView, `minSdk = 26`) and `gms` (system Android WebView + Firebase).
- **`formfactor`**: `mobile` (phones/tablets) and `tv` (Android TV). This dimension was added for Android TV support.

The full cross-product is `{foss, gms} × {mobile, tv} × {debug, release}` — four flavors (`gmsMobile`, `fossMobile`, `gmsTv`, `fossTv`), each with a `debug` and `release` build type.

### Form-factor build config
The `mobile` flavor sets `buildConfigField("boolean", "IS_TV", "false")`; the `tv` flavor sets it to `"true"`. The `tv` flavor also offsets its version code:

```kotlin
versionCode = getVersionAsInt("versionCode") + TV_VERSION_CODE_OFFSET // 1_000_000
```

Mobile and TV share a single `applicationId`, so Play multi-APK delivery requires distinct version codes to serve the correct APK per device class. Mobile keeps the base version code for release continuity, while TV is shifted into a separate range that still tracks the base as it increments. The offset (`1_000_000`) is far above any realistic base value, so the two ranges never overlap.

### Libraries mirror the dimension
The feature plugin (`dev.yahk.convention.feature`) mirrors only the `formfactor` dimension on every feature/core library (`mobile` + `tv`), because matching flavor names between the app and its libraries let Gradle's variant-aware dependency matching resolve automatically, and it is what enables `src/tv` source-sets in libraries. Libraries do **not** declare an `IS_TV` build config field — the runtime flag lives on the application `BuildConfig` and is exposed to libraries through the `AppConfig` abstraction. The app's extra `distribution` dimension auto-falls-back for libraries, so no `missingDimensionStrategy` is needed.
