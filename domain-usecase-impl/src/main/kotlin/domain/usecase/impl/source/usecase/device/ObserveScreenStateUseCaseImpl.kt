package domain.usecase.impl.source.usecase.device

import domain.core.source.model.ScreenStateModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.device.ObserveScreenStateUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveScreenStateUseCase] using [ConfigureRepository].
 *
 * Returns a cold [Flow] that emits every time the screen transitions between states.
 * The flow never terminates unless the underlying repository source cancels.
 *
 * @see ObserveScreenStateUseCase
 * @since 0.0.1
 */
@Single(binds = [ObserveScreenStateUseCase::class])
internal class ObserveScreenStateUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveScreenStateUseCase {
    override fun invoke(): Flow<ScreenStateModel> = configureRepository.observeScreenState()
}
