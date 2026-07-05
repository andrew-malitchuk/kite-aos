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
