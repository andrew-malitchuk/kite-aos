package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the reduce motion / disable animations setting.
 *
 * @since 0.0.6
 */
public interface SetReduceMotionUseCase {

    /**
     * Writes the reduce-motion preference to persistent storage.
     *
     * @param value `true` to suppress UI animations, `false` to enable them.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(value: Boolean): Optional
}
