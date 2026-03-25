package data.database.api.source.resource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import data.core.source.resource.Resource
import data.database.api.core.configure.DatabaseConfigure

/**
 * Database entity representing an application.
 *
 * @property id The unique identifier for the application (auto-generated).
 * @property name The name of the application.
 * @property packageName The Android package name of the application.
 * @property icon The resource ID or reference for the application icon.
 */
@Entity(tableName = DatabaseConfigure.Table.APPLICATION)
public data class ApplicationDatabase(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "icon")
    val icon: Int,
) : Resource
