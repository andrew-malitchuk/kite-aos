package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.core.source.model.AutoRebootModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetAutoRebootUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetAutoRebootUseCase] using [ConfigureRepository].
 *
 * Returns a default [AutoRebootModel] when no auto-reboot preference has been persisted yet,
 * ensuring callers always receive a valid model rather than null.
 *
 * @see GetAutoRebootUseCase
 * @since 0.0.1
 */
@Single(binds = [GetAutoRebootUseCase::class])
internal class GetAutoRebootUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetAutoRebootUseCase {
    override suspend fun invoke(): Result<AutoRebootModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getAutoReboot() ?: AutoRebootModel()
    }
}
