# Module: presentation-core-application

## Overview
This module serves as the **Composition Root** and primary entry point for the "kite-aos" Android application. It is responsible for orchestrating the initialization of all other modules, configuring global dependency injection, and defining the core application structure in the Android Manifest.

## Responsibilities
*   **Application Lifecycle**: Implements the `YahkApplication` class to handle process creation and global state initialization.
*   **DI Orchestration**: Centralizes the Koin dependency injection configuration by including modules from all layers (Data, Domain, and Presentation).
*   **System Integration**: Declares all necessary Android permissions and registers global components like foreground services and broadcast receivers.
*   **Visual Identity**: Defines the base application themes, including the Splash Screen and immersive kiosk mode styles.
*   **Service Bootstrapping**: Automatically launches critical background services (like MQTT and Motion detection) upon application start.

## Architecture
As the top-level module, it sits above all other layers. It does not contain business logic but rather "glues" the system together:
*   **`YahkApplication`**: The hub where Koin is started and system-level observers (like battery tracking) are registered.
*   **`appModule`**: A declarative Koin module that uses the `includes()` DSL to compose the entire project's dependency graph.
*   **Manifest**: Manages the application's security model (Permissions) and component definitions.

## Key Components
*   **`presentation.core.application.YahkApplication`**: The central application controller.
*   **`presentation.core.application.di.AppModule`**: The master registry for all dependencies in the system.

## Dependencies
This module depends on every feature and implementation module in the project to fulfill its role as the composition root. It utilizes the `dev.yahk.convention.application` plugin for standardized build configuration.
