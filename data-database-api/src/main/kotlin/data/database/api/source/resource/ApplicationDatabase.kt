package data.database.api.source.resource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import data.core.source.resource.Resource
import data.database.api.core.configure.DatabaseConfigure

/**
 * Database entity representing an installed application on the device.
 *
 * Mapped to the **`application`** table (see [DatabaseConfigure.Table.APPLICATION]).
 *
 * **Primary key strategy**: The [id] column uses Room's `autoGenerate = true`, so SQLite
 * assigns a monotonically increasing `ROWID` when [id] is `null` on insert.
 *
 * **Example row**:
 * ```
 * | id | name          | package_name              | icon         |
 * |----|---------------|---------------------------|--------------|
 * | 1  | "Calculator"  | "com.android.calculator2" | 0x7F080001   |
 * ```
 *
 * @property id The unique identifier for the application (auto-generated). `null` for new,
 *   unsaved entities.
 * @property name The human-readable display name of the application.
 * @property packageName The Android package name of the application (e.g., `com.example.app`).
 * @property icon The drawable resource ID representing the application icon.
 * @see data.database.impl.source.dao.ApplicationDao
 * @see DatabaseConfigure.Table.APPLICATION
 * @since 0.0.1
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
