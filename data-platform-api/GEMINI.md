# Module: data-platform-api

## Overview
This module defines the API layer for accessing Android platform-specific features and data. It provides interfaces for monitoring network connectivity and retrieving application information from the device.

## Responsibilities
*   **Connectivity Monitoring**: Defines the `ConnectivityObserver` interface and `NetworkStatus` enum for reactive network state tracking.
*   **Application Management**: Defines the `ApplicationPlatformSource` interface for listing and retrieving details of installed applications.
*   **Data Models**: Defines the `ApplicationPlatform` resource, representing device applications with their name, package name, and icon resource ID.

## Architecture
This module follows the Clean Architecture approach by decoupling platform-specific requirements from the implementation details of Android system services.

### Key Components
*   **`data.platform.api.source.connectivity.ConnectivityObserver`**: An interface that provides a `Flow` of `NetworkStatus` (Available, Losing, Lost, Unavailable).
*   **`data.platform.api.source.datasource.ApplicationPlatformSource`**: An interface to query the system for installed applications and their metadata.
*   **`data.platform.api.source.resource.ApplicationPlatform`**: A resource model for application-level data.

## Dependencies
*   **`common-core`**: For base utilities.
*   **`data-core`**: For the `Resource` marker interface.
