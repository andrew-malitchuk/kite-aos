package domain.usecase.api.source.usecase.streaming

import domain.core.source.model.StreamingModel

public interface GetStreamingConfigurationUseCase {
    public suspend operator fun invoke(): Result<StreamingModel>
}
