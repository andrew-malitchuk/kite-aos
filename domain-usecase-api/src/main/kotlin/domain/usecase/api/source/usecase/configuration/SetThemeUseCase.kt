package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ThemeModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for updating the application theme.
 *
 * @see GetThemeUseCase
 * @see ThemeModel
 * @since 0.0.1
 */
public interface SetThemeUseCase {
    /**
     * Writes the given theme to persistent storage, making it the active application theme.
     *
     * @param value The new [ThemeModel] (e.g., light, dark, or system-default) to persist.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(value: ThemeModel): Optional
}
