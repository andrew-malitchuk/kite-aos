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
