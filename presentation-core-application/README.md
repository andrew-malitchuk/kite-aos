# presentation-core-application

The entry point and composition root for the Home Kiosk application.

## Overview
This module contains the `Application` class and the primary `AndroidManifest.xml`. It doesn't implement features itself; instead, it stitches together the data, domain, and presentation features into a single executable APK.

## Features
- **Global DI Hub**: Initializes Koin and loads all sub-module dependency graphs.
- **App Startup**: Manages the transition from the splash screen to the main UI.
- **Persistent Services**: Starts and manages the lifecycle of the `MqttService` and other platform background tasks.
- **Permission Management**: Centralized declaration of hardware and system permissions.

## Key Files
- `YahkApplication.kt`: Bootstraps the app, DI, and system receivers.
- `di/AppModule.kt`: The master list of all Koin modules in the project.
- `AndroidManifest.xml`: The source of truth for app components and required permissions.

## Build Configuration
This module uses the `dev.yahk.convention.application` plugin, which applies the `com.android.application` plugin and configures signing, SDK versions, and base Compose dependencies.
