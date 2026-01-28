package domain.usecase.api.source.usecase.configuration

/**
 * Use case for retrieving the current application language locale code.
 */
public interface GetApplicationLanguageUseCase {
    /**
     * Retrieves the locale code (e.g., "en", "uk").
     *
     * @return A [Result] containing the locale code string, or null if not set.
     */
    public suspend operator fun invoke(): Result<String?>
}
