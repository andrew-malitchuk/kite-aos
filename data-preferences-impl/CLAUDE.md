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

## Interaction Preference *(@since 2.2.0)*
Persists the idle-reset timeout and the volume-button gesture (`presentation-feature-main`'s
"Interaction" behaviour). It follows the standard per-preference pattern:

1.  Schema: `src/main/proto/interaction_data.proto` (`message InteractionProtoModel { int32 inactivity_reset_minutes = 1; bool volume_gesture_enabled = 2; int32 volume_gesture_press_count = 3; }`)
2.  `InteractionProtoSerializer`
3.  `InteractionPreferenceStorage`
4.  `InteractionProtobufPreferenceMapper`
5.  `InteractionPreferenceSourceImpl`
6.  DI: `interactionDataStore()` (`@Named("interactionDataStore")`) persisting to `PreferenceConfigure.Filename.INTERACTION` (`interaction.pb`)

Unlike Resilience below, no encoding trick is needed: every field's off-state already coincides
with the proto3 zero value (idle reset off at `0` minutes, gesture off at `false`), and `0` is not a
meaningful press count, so it safely doubles as "unconfigured" and the mapper resolves it to `null`.

## Resilience Preference *(@since 2.2.0)*
Persists the T2 Survivability safeguards — crash relaunch, scheduled reload, memory recovery,
connection monitor, and WifiLock. It follows the standard per-preference pattern:

1.  Schema: `src/main/proto/resilience_data.proto` (`message ResilienceProtoModel { bool crash_relaunch_disabled = 1; bool scheduled_reload_enabled = 2; int32 scheduled_reload_hour_plus_one = 3; bool memory_recovery_disabled = 4; bool connection_monitor_disabled = 5; bool wifi_lock_disabled = 6; }`)
2.  `ResilienceProtoSerializer`
3.  `ResiliencePreferenceStorage`
4.  `ResilienceProtobufPreferenceMapper`
5.  `ResiliencePreferenceSourceImpl`
6.  DI: `resilienceDataStore()` (`@Named("resilienceDataStore")`) persisting to `PreferenceConfigure.Filename.RESILIENCE` (`resilience.pb`)

Most of these toggles default to **on**, which proto3 cannot express directly — a scalar has no
field presence, so a field added after some configs were already written reads back as its zero
value for all of them. `ResilienceProtobufPreferenceMapper` compensates with two encodings, the same
ones already used in `mqtt_data.proto` / `MqttProtobufPreferenceMapper`:

*   **Inverted booleans.** `crashRelaunchEnabled`, `memoryRecoveryEnabled`, `connectionMonitorEnabled`
    and `wifiLockEnabled` are stored as `*_disabled` fields. The mapper writes `true` only when the
    preference is explicitly `false` (`input.crashRelaunchEnabled == false`), so `null` — "never
    chosen" — round-trips as enabled, and reading writes `!input.crashRelaunchDisabled` etc. back
    into the positive-named preference field.
*   **Offset hour.** `scheduledReloadHour` is stored in `scheduled_reload_hour_plus_one` as
    `hour + 1`, because `0` is a real hour (midnight) and would otherwise be indistinguishable from
    "never configured". `toProtobuf` writes `input.scheduledReloadHour?.plus(1) ?: 0`; `toPreference`
    reads it back with `input.scheduledReloadHourPlusOne.takeIf { it > 0 }?.minus(1)`, so a stored
    `0` maps to `null` and the domain layer applies its own default.

`scheduledReloadEnabled` itself is the one field stored positively (`scheduled_reload_enabled`,
default off): the scheduled reload is opt-in, so its proto3 zero value (`false`) already matches the
feature's default.

## Dependencies
*   **`data-preferences-api`**: The contracts being implemented.
*   **`androidx.datastore`**: The underlying persistence engine.
*   **`com.google.protobuf`**: Support for Proto models and binary serialization.
*   **`common-core`**: Mapping utilities.
