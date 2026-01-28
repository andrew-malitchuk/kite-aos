package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.core.common.Optional

/**
 * Use case for updating the application's locale.
 */
public interface SetApplicationLanguageUseCase {
    /**
     * Sets the application language.
     *
     * @param localeCode The ISO language code (e.g., "uk", "en").
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(localeCode: String): Optional
}
