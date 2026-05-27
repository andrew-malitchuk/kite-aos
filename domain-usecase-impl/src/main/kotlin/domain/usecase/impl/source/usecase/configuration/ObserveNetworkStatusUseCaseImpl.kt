package domain.usecase.impl.source.usecase.configuration

import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.ObserveNetworkStatusUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveNetworkStatusUseCase] using [ConfigureRepository].
 *
 * @see ObserveNetworkStatusUseCase
 * @since 0.0.5
 */
@Single(binds = [ObserveNetworkStatusUseCase::class])
internal class ObserveNetworkStatusUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveNetworkStatusUseCase {
    override fun invoke(): Flow<Boolean> = configureRepository.observeNetworkStatus()
}
