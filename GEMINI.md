# Project Architecture: kite-aos

## Overview

Kite is a highly modular Android application designed for dedicated hardware usage (kiosk mode). It
features a modern tech stack based on Jetpack Compose, Orbit MVI, Koin DI, and Clean Architecture
principles.

## Architectural Layers

The project is organized into four primary layers, ensuring strict separation of concerns and high
testability.

### 1. Infrastructure (`build-logic`)

Contains the Gradle convention plugins that standardize build settings, dependency management, and
Kotlin/Android configurations across the repository.

- **Manifest**: [build-logic/GEMINI.md](build-logic/GEMINI.md)

### 2. Common & Core (`common-core`, `data-core`, `domain-core`)

Provides the foundational abstractions used by all other modules, including error handling monads,
asynchronous execution helpers, and base resource markers.

### 3. Data Layer (`data-*`)

Handles all external data sources including persistence (Room), networking (MQTT), platform APIs,
and user preferences (Proto DataStore).

- Follows the API/Impl pattern to decouple contracts from specific technologies.
- Orchestrates multiple sources through concrete Repository implementations.

### 4. Domain Layer (`domain-*`)

Contains the core business logic of the application. It is pure Kotlin and independent of any
platform or UI framework.

- **Repository API**: Defines the contracts for data access.
- **Use Cases**: Implements single, atomic business operations.

### 5. Presentation Layer (`presentation-*`)

The UI and platform integration layer built with Jetpack Compose and Orbit MVI.

- **Core modules**: Handle cross-cutting concerns like Styling, Localization, Navigation, and
  low-level Platform services (Motion detection).
- **Feature modules**: Independent screens (Main, Settings, Onboarding) that encapsulate their own
  UI logic and ViewModels.

---

## Technical Stack

- **UI**: Jetpack Compose with a custom Squircle-based design system.
- **Architecture**: MVI (Orbit framework) within a Clean Architecture structure.
- **Dependency Injection**: Koin (using KSP annotations).
- **Persistence**: Room (Database) and Proto DataStore (Preferences).
- **Communication**: MQTT for device telemetry and Home Assistant discovery.
- **Hardware Integration**: CameraX for intelligent motion detection.

---

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

---

## Building and Running

* **Build Project:** `./gradlew build`
* **Assemble Debug:** `./gradlew assembleDebug`
* **Module Docs**: Most modules contain their own `GEMINI.md` for deep-dive technical documentation.
