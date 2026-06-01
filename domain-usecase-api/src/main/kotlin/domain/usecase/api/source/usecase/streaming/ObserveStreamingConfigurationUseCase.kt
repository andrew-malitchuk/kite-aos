package domain.usecase.api.source.usecase.streaming

import domain.core.source.model.StreamingModel
import kotlinx.coroutines.flow.Flow

public interface ObserveStreamingConfigurationUseCase {
    public operator fun invoke(): Flow<StreamingModel?>
}
