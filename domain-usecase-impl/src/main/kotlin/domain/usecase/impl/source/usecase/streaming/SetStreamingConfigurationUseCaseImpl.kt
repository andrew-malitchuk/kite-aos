package domain.usecase.impl.source.usecase.streaming

import domain.core.source.monad.Failure
import domain.core.source.model.StreamingModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.streaming.SetStreamingConfigurationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetStreamingConfigurationUseCase] using [ConfigureRepository].
 *
 * Persists the streaming configuration; failures are mapped to
 * [domain.core.source.monad.Failure.Technical.Preference].
 *
 * @see SetStreamingConfigurationUseCase
 * @since 0.0.1
 */
@Single(binds = [SetStreamingConfigurationUseCase::class])
internal class SetStreamingConfigurationUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetStreamingConfigurationUseCase {
    override suspend fun invoke(streaming: StreamingModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setStreaming(streaming)
    }
}
