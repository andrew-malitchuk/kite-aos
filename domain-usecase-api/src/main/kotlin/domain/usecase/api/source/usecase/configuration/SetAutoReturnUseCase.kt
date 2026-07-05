package domain.usecase.api.source.usecase.configuration

import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the auto-return kiosk setting.
 *
 * @since 0.0.4
 */
public interface SetAutoReturnUseCase {

    /**
     * Writes the auto-return preference to persistent storage.
     *
     * @param value `true` to enable automatic return to the kiosk after leaving to an
     *   external app, `false` to disable it.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(value: Boolean): Optional
}
