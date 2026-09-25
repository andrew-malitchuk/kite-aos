# data-preferences-impl

Implementation of application preferences using Jetpack DataStore and Protocol Buffers.

## Features

*   **Type Safety**: All settings are backed by `.proto` schemas.
*   **Reactive**: Settings are exposed as `Flows` for real-time UI updates.
*   **Transaction-safe**: DataStore ensures atomic updates and handles file system complexities.
*   **Clean Separation**: Domain models are never exposed to the disk format directly; mappers ensure binary compatibility.

## Internal Workflow

When a preference is requested:
1.  `Storage` reads the data from the `.pb` file via `DataStore`.
2.  `SourceImpl` takes the generated Proto model and passes it to a `Mapper`.
3.  The `Mapper` returns a clean API `Resource` to the caller.

## Adding a New Preference

1.  Define the schema in `src/main/proto`.
2.  Implement a `Serializer` in `core/serializer`.
3.  Create a `Storage` class in `source/storage`.
4.  Implement a `Mapper` in `core/mapper`.
5.  Create the `SourceImpl` in `source/datasource`.
6.  Register the `DataStore` and bindings in `DataPreferencesImplModule`.

## Camera-Source Preference *(@since 1.4.0)*

The camera-source preference is a full example of the workflow above. It persists the camera chosen for motion detection and MJPEG streaming (`auto`, `front`, `rear`, or `external`) — on all form-factors, not just TV — through:

*   Schema: `src/main/proto/camera_data.proto` (`message CameraProtoModel { string mode = 1; }`)
*   `CameraProtoSerializer`, `CameraPreferenceStorage`, `CameraProtobufPreferenceMapper`, `CameraPreferenceSourceImpl`
*   DI: `cameraDataStore()` (`@Named("cameraDataStore")`) persisting to `camera.pb` (`PreferenceConfigure.Filename.CAMERA`)

## Interaction Preference *(@since 2.2.0)*

Persists the idle-reset timeout and volume-button gesture, following the same workflow:

*   Schema: `src/main/proto/interaction_data.proto` (`message InteractionProtoModel { int32 inactivity_reset_minutes = 1; bool volume_gesture_enabled = 2; int32 volume_gesture_press_count = 3; }`)
*   `InteractionProtoSerializer`, `InteractionPreferenceStorage`, `InteractionProtobufPreferenceMapper`, `InteractionPreferenceSourceImpl`
*   DI: `interactionDataStore()` (`@Named("interactionDataStore")`) persisting to `interaction.pb` (`PreferenceConfigure.Filename.INTERACTION`)

No proto3 encoding trick is needed here — every default already coincides with the zero value.

## Resilience Preference *(@since 2.2.0)*

Persists the T2 Survivability safeguards (crash relaunch, scheduled reload, memory recovery, connection monitor, WifiLock), following the same workflow:

*   Schema: `src/main/proto/resilience_data.proto` (`message ResilienceProtoModel { bool crash_relaunch_disabled = 1; bool scheduled_reload_enabled = 2; int32 scheduled_reload_hour_plus_one = 3; bool memory_recovery_disabled = 4; bool connection_monitor_disabled = 5; bool wifi_lock_disabled = 6; }`)
*   `ResilienceProtoSerializer`, `ResiliencePreferenceStorage`, `ResilienceProtobufPreferenceMapper`, `ResiliencePreferenceSourceImpl`
*   DI: `resilienceDataStore()` (`@Named("resilienceDataStore")`) persisting to `resilience.pb` (`PreferenceConfigure.Filename.RESILIENCE`)

Most of these toggles default **on**, which proto3's zero-value default can't express, so
`ResilienceProtobufPreferenceMapper` stores them inverted (`*_disabled`) — an explicit `false` from
the preference layer is what writes a `true` disabled flag; `null` round-trips as enabled. The
scheduled-reload hour is stored as `hour + 1` (`scheduled_reload_hour_plus_one`) so hour `0`
(midnight) is never confused with "unconfigured".
