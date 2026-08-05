# Module: domain-core

## Overview
This module serves as the central core of the Domain layer. It contains the fundamental building blocks used across all domain-related modules, including base model interfaces, central data models (entities), and core domain logic such as standardized error handling.

## Responsibilities
*   **Domain Models**: Defines the core data entities used by the business logic, such as `ApplicationModel`, `DashboardModel`, `ThemeModel`, `MoveDetectorModel`, and `MqttModel`.
*   **Error Handling (Monads)**: Provides the `Failure` sealed class hierarchy to standardize how errors (Technical vs. Logic) are represented and handled within the domain layer.
*   **Base Definitions**: Defines the `Model` interface, which acts as a marker for all domain models.

## Architecture
This is a pure Kotlin module that sits at the center of the architecture. It does not depend on any other layers (Data or Presentation), ensuring that the business logic remains decoupled from infrastructure and UI concerns.

### Key Components
*   **`domain.core.source.monad.Failure`**: A structured way to represent errors.
    *   `Technical`: For infrastructure issues (Database, Network, Platform).
    *   `Logic`: For business rules or "Not Found" scenarios.
*   **`domain.core.source.model.*`**: Data classes representing the state of various components like Dock position, Onboarding status, and Application info.
*   **`domain.core.source.model.ScreenStateModel`**: Sealed model of the kiosk display state with three subtypes (`@since 1.2.0` for `DarkOverlay`):
    *   `Active`: the normal interactive kiosk view.
    *   `Screensaver`: the inactivity overlay that may show images and a clock.
    *   `DarkOverlay`: a plain dark overlay shown as a stand-in for powering the panel off, used on devices that cannot lock or turn the screen off (e.g. Android TV). Unlike `Screensaver` it never renders images or a clock.
*   **`domain.core.source.model.CameraSourceModel`** (`@since 1.4.0`): Enum (`Auto` / `Front` / `Rear` / `External`) representing the user preference that overrides the platform's automatic, capability-based camera selection for motion detection and MJPEG streaming. Applies to all form-factors (including phones with USB-OTG webcams, not just TV). Each entry carries a stable `mode` string persisted in Proto DataStore; an unavailable forced choice falls back to `Auto` at runtime.

## Dependencies
*   **`common-core`**: Provides shared utilities and base infrastructure needed by the domain core.
