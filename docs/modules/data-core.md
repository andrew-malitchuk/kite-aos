# data-core

Core abstractions and markers for the Data layer of the "kite-aos" project.

## Features

- **Resource Marker**: Provides the `Resource` interface to unify data model types across different data modules (API and implementation).

## Usage

Implement the `Resource` interface for any data class that represents a resource in the data layer (e.g., preferences, database entities).

```kotlin
data class MyDataResource(val id: String) : Resource
```
