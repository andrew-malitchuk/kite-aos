package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.AutoRebootModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for persisting the scheduled auto-reboot configuration.
 *
 * Passing `null` clears any previously saved schedule, effectively disabling auto-reboot.
 *
 * @see GetAutoRebootUseCase
 * @see ObserveAutoRebootUseCase
 * @see AutoRebootModel
 * @since 0.0.5
 */
public interface SetAutoRebootUseCase {

    /**
     * Writes the given [model] to persistent storage.
     *
     * @param model The new auto-reboot schedule to save, or `null` to disable auto-reboot.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(model: AutoRebootModel?): Optional
}
