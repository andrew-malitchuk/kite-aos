# Module: data-preferences-impl

## Overview
This module provides the concrete implementation for the preference data sources defined in `data-preferences-api`. It utilizes **Jetpack DataStore (Proto DataStore)** to persist settings using Protocol Buffers, ensuring type safety and efficient serialization.

## Responsibilities
*   **Persistent Storage**: Implements `BasePreferenceStorage` using `DataStore<T>`.
*   **Serialization**: Manages the conversion between binary streams and Proto models via `Serializer<T>`.
*   **Data Mapping**: Provides mappers to bridge the gap between generated Proto classes (DTOs) and API models (Resources).
*   **Dependency Injection**: Configures Koin to provide specific `DataStore` instances and bind implementations to API interfaces.

## Architecture
The implementation follows a strict layered pattern for each preference type:
1.  **Proto Definition (`src/main/proto`)**: Defines the schema.
2.  **Serializer**: Handles Protobuf I/O.
3.  **Storage**: A DAO-like class wrapping `DataStore` to provide a clean Flow/suspend API.
4.  **Mapper**: Bidirectional conversion between Proto models and API models.
5.  **Source Implementation**: Orchestrates Storage and Mapper to fulfill the API contract.

## Key Components
*   **`data.preferences.impl.source.datasource.base.BasePreferenceSourceImpl`**: A generic base class that automates the orchestration of storage and mapping.
*   **`data.preferences.impl.di.DataPreferencesImplModule`**: Central DI configuration using `DataStoreFactory`. Each preference gets its own `@Named` DataStore provider (e.g. `@Named("cameraDataStore")`).
*   **`PreferenceConfigure`**: Constants for DataStore filenames.

## Camera-Source Preference *(@since 1.4.0)*
Persists the user's chosen camera source (`auto` / `front` / `rear` / `external`) for motion detection and MJPEG streaming; available on all form-factors, not just TV. It follows the standard per-preference pattern: `camera_data.proto` (`message CameraProtoModel { string mode = 1; }`) → `CameraProtoSerializer` → `CameraPreferenceStorage` → `CameraProtobufPreferenceMapper` → `CameraPreferenceSourceImpl`, backed by the `cameraDataStore()` provider (`@Named("cameraDataStore")`) persisting to `PreferenceConfigure.Filename.CAMERA` (`camera.pb`).

## Dependencies
*   **`data-preferences-api`**: The contracts being implemented.
*   **`androidx.datastore`**: The underlying persistence engine.
*   **`com.google.protobuf`**: Support for Proto models and binary serialization.
*   **`common-core`**: Mapping utilities.
