package domain.usecase.impl.source.usecase.streaming

import domain.core.source.model.StreamingModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.streaming.ObserveStreamingConfigurationUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [ObserveStreamingConfigurationUseCase::class])
internal class ObserveStreamingConfigurationUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveStreamingConfigurationUseCase {
    override fun invoke(): Flow<StreamingModel?> = configureRepository.observeStreaming()
}
