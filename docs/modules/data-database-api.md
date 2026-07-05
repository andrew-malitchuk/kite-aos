# data-database-api

Contracts and entities for the relational database of the "kite-aos" project.

## Features
- **Strongly Typed Entities**: Room entities defined for structured data persistence.
- **Reactive Data Sources**: Abstract source interfaces providing `Flow`-based observation of database changes.
- **Centralized Config**: Unified management of database and table names.

## Usage
Implement the `DatabaseSource` interface in the implementation module to provide concrete data access logic. Use the entities defined here throughout the data layer.
