package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.InteractionModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetInteractionUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetInteractionUseCase] using [ConfigureRepository].
 *
 * Substitutes a default [InteractionModel] when nothing has been persisted yet, so callers never have
 * to distinguish "not configured" from "configured to the defaults".
 *
 * @see GetInteractionUseCase
 * @since 2.2.0
 */
@Single(binds = [GetInteractionUseCase::class])
internal class GetInteractionUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetInteractionUseCase {
    override suspend operator fun invoke(): Result<InteractionModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getInteraction() ?: InteractionModel()
    }
}
