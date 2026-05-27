package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing an application that can be displayed or selected.
 *
 * @property id Unique identifier for the application (null if not persisted).
 * @property name User-friendly name of the application.
 * @property packageName Android package name (unique identifier on the system).
 * @property icon Resource ID or reference for the application's icon.
 * @property chosen Indicates if the application has been selected for the kiosk dashboard.
 *
 * @see Model
 * @since 0.0.1
 */
public data class ApplicationModel(
    val id: Int?,
    val name: String,
    val packageName: String,
    val icon: Int,
    val chosen: Boolean? = null,
) : Model
