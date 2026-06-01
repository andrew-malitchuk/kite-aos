package domain.usecase.impl.source.usecase.device

import domain.core.source.model.ScreenStateModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.device.ObserveScreenStateUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [ObserveScreenStateUseCase::class])
internal class ObserveScreenStateUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveScreenStateUseCase {
    override fun invoke(): Flow<ScreenStateModel> = configureRepository.observeScreenState()
}
