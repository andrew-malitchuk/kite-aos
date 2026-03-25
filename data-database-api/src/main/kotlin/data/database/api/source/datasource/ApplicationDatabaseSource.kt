package data.database.api.source.datasource

import data.database.api.source.datasource.base.DatabaseSource
import data.database.api.source.resource.ApplicationDatabase

/**
 * Specialized [DatabaseSource] for managing [ApplicationDatabase] resources.
 */
public abstract class ApplicationDatabaseSource : DatabaseSource<ApplicationDatabase>()
