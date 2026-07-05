package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ThemeModel

/**
 * Use case for retrieving the current application theme.
 *
 * @see SetThemeUseCase
 * @see ObserveThemeUseCase
 * @see ThemeModel
 * @since 0.0.1
 */
public interface GetThemeUseCase {
    /**
     * Reads the active theme setting from persistent storage.
     *
     * @return `Result.success` wrapping the current [ThemeModel], or `Result.failure`
     *   with a `Failure` if the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<ThemeModel>
}
