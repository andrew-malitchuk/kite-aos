package domain.usecase.api.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel
import domain.usecase.api.source.common.Optional

public interface SetScreensaverUseCase {
    public suspend operator fun invoke(screensaver: ScreensaverModel): Optional
}
