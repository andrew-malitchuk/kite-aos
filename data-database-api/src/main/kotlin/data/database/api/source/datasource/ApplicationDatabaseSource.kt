package data.database.api.source.datasource

import data.database.api.source.datasource.base.DatabaseSource
import data.database.api.source.resource.ApplicationDatabase

/**
 * Specialized [DatabaseSource] for managing [ApplicationDatabase] resources.
 *
 * Serves as the API-layer contract for application database operations. Concrete
 * implementations reside in the `data-database-impl` module.
 *
 * @see DatabaseSource
 * @see ApplicationDatabase
 * @see data.database.impl.source.datasource.ApplicationDatabaseSourceImpl
 * @since 0.0.1
 */
public abstract class ApplicationDatabaseSource : DatabaseSource<ApplicationDatabase>()
