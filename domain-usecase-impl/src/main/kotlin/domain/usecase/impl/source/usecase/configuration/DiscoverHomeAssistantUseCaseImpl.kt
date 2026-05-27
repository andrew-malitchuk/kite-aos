package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.HomeAssistantInstanceModel
import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.DiscoverHomeAssistantUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [DiscoverHomeAssistantUseCase] using [ConfigureRepository].
 *
 * @see DiscoverHomeAssistantUseCase
 * @since 0.0.5
 */
@Single(binds = [DiscoverHomeAssistantUseCase::class])
internal class DiscoverHomeAssistantUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : DiscoverHomeAssistantUseCase {
    override suspend fun invoke(): Result<List<HomeAssistantInstanceModel>> = resultLauncher(
        errorMapper = Failure.Technical::Network,
    ) {
        configureRepository.discoverHomeAssistant()
    }
}
