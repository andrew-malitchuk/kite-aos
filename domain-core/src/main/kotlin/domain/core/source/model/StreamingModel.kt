package domain.core.source.model

import domain.core.source.model.base.Model

public data class StreamingModel(
    val enabled: Boolean? = null,
    val port: Int? = null,
    val quality: Int? = null,
    val fps: Int? = null,
    val rotation: Int? = null,
) : Model
