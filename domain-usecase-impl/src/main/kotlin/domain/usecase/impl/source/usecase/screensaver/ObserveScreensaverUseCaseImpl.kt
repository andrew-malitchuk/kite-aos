package domain.usecase.impl.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.screensaver.ObserveScreensaverUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [ObserveScreensaverUseCase::class])
internal class ObserveScreensaverUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveScreensaverUseCase {
    override fun invoke(): Flow<ScreensaverModel?> = configureRepository.observeScreensaver()
}
