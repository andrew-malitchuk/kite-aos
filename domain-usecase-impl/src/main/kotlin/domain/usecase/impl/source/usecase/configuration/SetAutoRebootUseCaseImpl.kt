package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.core.source.model.AutoRebootModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.configuration.SetAutoRebootUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetAutoRebootUseCase] using [ConfigureRepository].
 *
 * Passing `null` clears the persisted auto-reboot configuration.
 *
 * @see SetAutoRebootUseCase
 * @since 0.0.1
 */
@Single(binds = [SetAutoRebootUseCase::class])
internal class SetAutoRebootUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetAutoRebootUseCase {
    override suspend fun invoke(model: AutoRebootModel?): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setAutoReboot(model)
    }
}
