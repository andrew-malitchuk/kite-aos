package domain.usecase.api.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel

public interface GetScreensaverUseCase {
    public suspend operator fun invoke(): Result<ScreensaverModel?>
}
