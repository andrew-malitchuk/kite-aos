package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional

/**
 * Use case for updating the application's locale.
 *
 * @see GetApplicationLanguageUseCase
 * @since 0.0.1
 */
public interface SetApplicationLanguageUseCase {
    /**
     * Writes the given locale code to persistent storage, making it the active app language.
     *
     * @param localeCode The BCP-47 / ISO 639-1 language tag to apply (e.g., `"en"`, `"uk"`).
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(localeCode: String): Optional
}
