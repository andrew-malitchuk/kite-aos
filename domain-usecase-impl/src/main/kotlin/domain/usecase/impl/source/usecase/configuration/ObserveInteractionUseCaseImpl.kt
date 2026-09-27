package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.InteractionModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.ObserveInteractionUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveInteractionUseCase] using [ConfigureRepository].
 *
 * @see ObserveInteractionUseCase
 * @since 2.2.0
 */
@Single(binds = [ObserveInteractionUseCase::class])
internal class ObserveInteractionUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveInteractionUseCase {
    override operator fun invoke(): Flow<InteractionModel?> = configureRepository.observeInteraction()
}
