package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.AutoRebootModel

/**
 * Use case for retrieving the scheduled auto-reboot configuration.
 *
 * @see SetAutoRebootUseCase
 * @see ObserveAutoRebootUseCase
 * @see AutoRebootModel
 * @since 0.0.5
 */
public interface GetAutoRebootUseCase {

    /**
     * Reads the persisted auto-reboot settings from storage.
     *
     * @return `Result.success` wrapping the current [AutoRebootModel], or `Result.failure`
     *   with a `Failure` if the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<AutoRebootModel>
}
