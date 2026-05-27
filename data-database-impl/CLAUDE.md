# Module: data-database-impl

## Overview
This module provides the concrete implementation of the persistent database defined in `data-database-api`. It utilizes **Room Persistence Library** to manage the underlying SQLite database.

## Responsibilities
*   **Room Database Implementation**: Defines the `RoomDatabase` class, specifying entities, versioning, and migrations.
*   **Data Access Objects (DAOs)**: Implements the DAOs for performing SQLite queries.
*   **Source Implementation**: Provides concrete implementations of the API's data source interfaces (e.g., `ApplicationDatabaseSourceImpl`), bridging DAOs to the repository layer.
*   **Dependency Injection**: Configures Koin modules to provide the database instance and DAOs.

## Architecture
This module provides the "Implementation" side of the database contract. It maps DAO results to the API entities and handles the low-level Room setup.

### Key Components
*   **`data.database.impl.core.database.RoomDatabase`**: The central Room database class.
*   **`data.database.impl.source.dao.*`**: Room DAOs for each table.
*   **`data.database.impl.source.datasource.*`**: Implementations of `DatabaseSource` interfaces from the API module.
*   **`data.database.impl.di.DataDatabaseImplModule`**: Koin module for database and DAO provision.

## Dependencies
*   **`data-database-api`**: Implements the contracts defined there.
*   **`androidx.room`**: The core persistence library and its KSP compiler.
*   **`koin-annotations`**: For dependency injection.
