package domain.usecase.api.source.usecase.configuration

/**
 * Use case for retrieving the reduce motion / disable animations setting.
 *
 * Returns `true` if animations should be disabled across the UI, `false` otherwise.
 *
 * @since 0.0.6
 */
public interface GetReduceMotionUseCase {

    /**
     * Reads the reduce-motion flag from persistent storage.
     *
     * @return `Result.success(true)` if animations should be suppressed across the UI,
     *   `Result.success(false)` if animations are enabled, or `Result.failure` with a
     *   `Failure` if the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<Boolean>
}
