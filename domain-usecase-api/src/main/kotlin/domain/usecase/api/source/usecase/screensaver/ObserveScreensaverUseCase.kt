package domain.usecase.api.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel
import kotlinx.coroutines.flow.Flow

public interface ObserveScreensaverUseCase {
    public operator fun invoke(): Flow<ScreensaverModel?>
}
