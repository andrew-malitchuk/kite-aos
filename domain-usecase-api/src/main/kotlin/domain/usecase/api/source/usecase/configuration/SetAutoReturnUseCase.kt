package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the auto-return kiosk setting.
 *
 * @since 0.0.4
 */
public interface SetAutoReturnUseCase {
    public suspend operator fun invoke(value: Boolean): Optional
}
