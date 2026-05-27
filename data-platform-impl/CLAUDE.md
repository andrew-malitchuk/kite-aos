# Module: data-platform-impl

## Overview
This module provides the concrete Android-specific implementation of the interfaces defined in `data-platform-api`. It interacts directly with Android System Services such as `ConnectivityManager` and `PackageManager`.

## Responsibilities
*   **Network Status Implementation**: Implements `ConnectivityObserver` using `ConnectivityManager.NetworkCallback` and `callbackFlow` for robust, reactive updates.
*   **Application Discovery Implementation**: Implements `ApplicationPlatformSource` using the `PackageManager` to filter and retrieve applications that have a launch intent.
*   **System Mapping**: Provides mappers to convert Android framework objects (e.g., `ApplicationInfo`) into platform-agnostic models.
*   **Dependency Injection**: Configures Koin modules for platform-specific source provision.

## Architecture
This module implements the Platform API using native Android APIs. It bridges the gap between the low-level Android SDK and the application's clean architecture.

### Key Components
*   **`data.platform.impl.source.connectivity.ConnectivityObserverImpl`**: Handles the registration and unregistration of network callbacks.
*   **`data.platform.impl.source.datasource.ApplicationPlatformSourceImpl`**: Interacts with `PackageManager` to enumerate installed apps.
*   **`data.platform.impl.core.mapper.ApplicationSystemPlatformMapper`**: Maps system application info to the API's `ApplicationPlatform` model.
*   **`data.platform.impl.di.DataPlatformImplModule`**: Koin module for platform implementations.

## Dependencies
*   **`data-platform-api`**: Implements the contracts defined there.
*   **`common-core`**: For mapping abstractions.
*   **`data-core`**: For resource markers.
*   **Android SDK**: Uses `ConnectivityManager` and `PackageManager`.
