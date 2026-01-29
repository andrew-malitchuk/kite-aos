package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ThemeModel
import domain.usecase.api.core.common.Optional

/**
 * Use case for updating the application theme.
 */
public interface SetThemeUseCase {
    /**
     * @param value The new [ThemeModel] to apply.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(value: ThemeModel): Optional
}