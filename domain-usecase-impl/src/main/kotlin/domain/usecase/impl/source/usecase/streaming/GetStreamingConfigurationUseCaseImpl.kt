package domain.usecase.impl.source.usecase.streaming

import domain.core.source.monad.Failure
import domain.core.source.model.StreamingModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.streaming.GetStreamingConfigurationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetStreamingConfigurationUseCase] using [ConfigureRepository].
 *
 * Returns a default [StreamingModel] when no streaming configuration has been persisted yet,
 * ensuring callers always receive a valid model rather than null.
 *
 * @see GetStreamingConfigurationUseCase
 * @since 0.0.1
 */
@Single(binds = [GetStreamingConfigurationUseCase::class])
internal class GetStreamingConfigurationUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetStreamingConfigurationUseCase {
    override suspend fun invoke(): Result<StreamingModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getStreaming() ?: StreamingModel()
    }
}
