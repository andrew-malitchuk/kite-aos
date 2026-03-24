package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ThemeModel

/**
 * Use case for retrieving the current application theme.
 */
public interface GetThemeUseCase {
    /**
     * @return A [Result] containing the [ThemeModel].
     */
    public suspend operator fun invoke(): Result<ThemeModel>
}
