# Module: data-database-api

## Overview
This module defines the API layer for the application's persistent relational database. It contains the data models (entities), configuration constants, and abstract data source interfaces.

## Responsibilities
*   **Database Schema**: Defines Room `@Entity` classes representing the tables in the database (e.g., `ApplicationDatabase`).
*   **Configuration**: Centralizes database and table names in `DatabaseConfigure`.
*   **Data Source Interfaces**: Defines abstract `DatabaseSource` and specialized interfaces (e.g., `ApplicationDatabaseSource`) for CRUD operations.

## Architecture
This module follows the Clean Architecture approach by decoupling the definition of data structures and access patterns (API) from the concrete Room implementation.

### Key Components
*   **`data.database.api.source.resource.*`**: Room entities representing database tables.
*   **`data.database.api.source.datasource.base.DatabaseSource`**: Base abstract class for database operations (save, getAll, observe, delete).
*   **`data.database.api.core.configure.DatabaseConfigure`**: Constants for database and table naming.

## Dependencies
*   **`data-core`**: For the base `Resource` marker interface.
*   **`androidx.room:room-runtime`**: For entity annotations.
