package domain.usecase.impl.source.usecase.device

import domain.core.source.monad.Failure
import domain.core.source.model.ScreenStateModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.device.EmitScreenStateUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [EmitScreenStateUseCase] using [ConfigureRepository].
 *
 * Pushes a [ScreenStateModel] event into the shared screen-state stream so that
 * observers (e.g. the MQTT service) can react to screen on/off transitions.
 * Failures are mapped to [domain.core.source.monad.Failure.Technical.Platform].
 *
 * @see EmitScreenStateUseCase
 * @since 0.0.1
 */
@Single(binds = [EmitScreenStateUseCase::class])
internal class EmitScreenStateUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : EmitScreenStateUseCase {
    override suspend fun invoke(state: ScreenStateModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Platform,
    ) {
        configureRepository.emitScreenState(state)
    }
}
