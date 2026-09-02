package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.InteractionModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.configuration.SetInteractionUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetInteractionUseCase] using [ConfigureRepository].
 *
 * @see SetInteractionUseCase
 * @since 2.2.0
 */
@Single(binds = [SetInteractionUseCase::class])
internal class SetInteractionUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetInteractionUseCase {
    override suspend operator fun invoke(interaction: InteractionModel?): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setInteraction(interaction)
    }
}
