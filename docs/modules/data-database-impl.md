# data-database-impl

Room-based implementation of the database contracts for the "kite-aos" project.

## Features
- **Room Persistence**: Robust SQL-based storage using Android's Room library.
- **Automated DI**: Uses Koin annotations for easy integration of DAOs and Data Sources.
- **Reactive Stream Implementation**: Concrete implementation of `Flow` observers for real-time UI updates.

## Setup
The database is initialized via Koin in `DataDatabaseImplModule`. It uses destructive migrations for simplicity during early development stages (configurable in the module).
