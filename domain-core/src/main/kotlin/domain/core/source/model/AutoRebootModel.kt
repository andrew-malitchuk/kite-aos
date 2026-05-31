package domain.core.source.model

import domain.core.source.model.base.Model

public data class AutoRebootModel(
    val enabled: Boolean? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val intervalDays: Int? = null,
) : Model
