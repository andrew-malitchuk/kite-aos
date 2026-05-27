# Module: data-core

## Overview
This module serves as the foundational core for the Data layer. It defines common abstractions and markers used by various data sources and repositories across the application.

## Responsibilities
*   **Base Definitions**: Defines the `Resource` interface, which acts as a marker for all data models used in the API and implementation layers of the data modules.

## Architecture
This is a pure Kotlin module that sits at the foundation of the Data layer. It provides the essential types required for defining data contracts without being tied to any specific persistence or networking technology.

### Key Components
*   **`data.core.source.resource.Resource`**: A marker interface for data resources.

## Dependencies
*   None (Pure Kotlin)
