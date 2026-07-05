package domain.usecase.api.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the screensaver configuration.
 *
 * @see GetScreensaverUseCase
 * @see ObserveScreensaverUseCase
 * @see ScreensaverModel
 * @since 0.0.7
 */
public interface SetScreensaverUseCase {

    /**
     * Writes the given screensaver configuration to persistent storage.
     *
     * @param screensaver The new screensaver settings (type, idle timeout, clock style, etc.)
     *   to persist.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(screensaver: ScreensaverModel): Optional
}
