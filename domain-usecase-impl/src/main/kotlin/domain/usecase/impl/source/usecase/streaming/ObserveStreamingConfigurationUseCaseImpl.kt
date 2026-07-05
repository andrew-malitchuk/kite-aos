package domain.usecase.impl.source.usecase.streaming

import domain.core.source.model.StreamingModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.streaming.ObserveStreamingConfigurationUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveStreamingConfigurationUseCase] using [ConfigureRepository].
 *
 * Emits `null` when no streaming configuration is stored or when the stored value is cleared.
 *
 * @see ObserveStreamingConfigurationUseCase
 * @since 0.0.1
 */
@Single(binds = [ObserveStreamingConfigurationUseCase::class])
internal class ObserveStreamingConfigurationUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveStreamingConfigurationUseCase {
    override fun invoke(): Flow<StreamingModel?> = configureRepository.observeStreaming()
}
