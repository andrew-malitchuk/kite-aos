package domain.usecase.impl.source.usecase.streaming

import domain.core.source.monad.Failure
import domain.core.source.model.StreamingModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.streaming.SetStreamingConfigurationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

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
