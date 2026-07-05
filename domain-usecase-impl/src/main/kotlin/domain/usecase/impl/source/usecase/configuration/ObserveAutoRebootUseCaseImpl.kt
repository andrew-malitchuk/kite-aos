package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.AutoRebootModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.ObserveAutoRebootUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveAutoRebootUseCase] using [ConfigureRepository].
 *
 * Emits `null` when no auto-reboot preference is stored and the upstream source clears its value.
 *
 * @see ObserveAutoRebootUseCase
 * @since 0.0.1
 */
@Single(binds = [ObserveAutoRebootUseCase::class])
internal class ObserveAutoRebootUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveAutoRebootUseCase {
    override fun invoke(): Flow<AutoRebootModel?> = configureRepository.observeAutoReboot()
}
