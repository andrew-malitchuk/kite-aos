package domain.usecase.api.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel

/**
 * Use case for retrieving the current screensaver configuration.
 *
 * @see SetScreensaverUseCase
 * @see ObserveScreensaverUseCase
 * @see ScreensaverModel
 * @since 0.0.7
 */
public interface GetScreensaverUseCase {

    /**
     * Reads the screensaver configuration from persistent storage.
     *
     * @return `Result.success` wrapping the current [ScreensaverModel], or `null` if no
     *   screensaver has been configured. Returns `Result.failure` with a `Failure` if
     *   the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<ScreensaverModel?>
}
