package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the reduce motion / disable animations setting.
 *
 * @since 0.0.6
 */
public interface SetReduceMotionUseCase {
    public suspend operator fun invoke(value: Boolean): Optional
}
