package domain.usecase.api.source.usecase.configuration

/**
 * Use case for retrieving the reduce motion / disable animations setting.
 *
 * Returns `true` if animations should be disabled across the UI, `false` otherwise.
 *
 * @since 0.0.6
 */
public interface GetReduceMotionUseCase {
    public suspend operator fun invoke(): Result<Boolean>
}
