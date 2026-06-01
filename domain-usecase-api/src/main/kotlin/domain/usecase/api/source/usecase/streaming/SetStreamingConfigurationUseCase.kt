package domain.usecase.api.source.usecase.streaming

import domain.core.source.model.StreamingModel
import domain.usecase.api.source.common.Optional

public interface SetStreamingConfigurationUseCase {
    public suspend operator fun invoke(streaming: StreamingModel): Optional
}
