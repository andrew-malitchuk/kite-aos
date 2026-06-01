package domain.usecase.api.source.usecase.device

import domain.core.source.model.ScreenStateModel
import kotlinx.coroutines.flow.Flow

public interface ObserveScreenStateUseCase {
    public operator fun invoke(): Flow<ScreenStateModel>
}
