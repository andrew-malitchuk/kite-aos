package domain.usecase.api.source.usecase.device

import domain.core.source.model.ScreenStateModel

public interface EmitScreenStateUseCase {
    public suspend operator fun invoke(state: ScreenStateModel)
}
