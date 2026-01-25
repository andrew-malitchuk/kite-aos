# domain-core

Central domain models and core logic for the "kite-aos" project.

## Features

- **Domain Entities**: Pure Kotlin data classes representing the application's business state (Theme, Dashboard, Dock, Motion, MQTT, etc.).
- **Standardized Error Handling**: A structured `Failure` hierarchy to distinguish between technical issues (network, database) and business logic errors.
- **Pure Logic**: Zero dependencies on Android or external infrastructure, ensuring business rules are easily testable.

## Core Components

### Models
All domain models implement the `Model` marker interface:
- `ApplicationModel`: Installed and chosen apps.
- `DashboardModel`: URL configurations.
- `ThemeModel`: UI theme modes (Light, Dark, Material You).
- `MoveDetectorModel`: Motion detection sensitivity and delays.
- `MqttModel`: Broker configuration and credentials.

### Failure Handling
Uses the `Failure` sealed class to represent errors:
- `Failure.Technical`: Database, Network, Platform, or Preference issues.
- `Failure.Logic`: Business rules violations or "Not Found" scenarios.

## Usage

Use these models across all domain use cases and repository contracts. Use the `Failure` class to propagate errors back to the presentation layer in a structured way.
