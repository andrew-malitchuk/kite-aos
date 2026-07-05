package domain.usecase.impl.source.usecase.screensaver

import domain.core.source.model.ScreensaverModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.screensaver.ObserveScreensaverUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveScreensaverUseCase] using [ConfigureRepository].
 *
 * Emits `null` when no screensaver preference is stored or when the stored value is cleared.
 *
 * @see ObserveScreensaverUseCase
 * @since 0.0.1
 */
@Single(binds = [ObserveScreensaverUseCase::class])
internal class ObserveScreensaverUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveScreensaverUseCase {
    override fun invoke(): Flow<ScreensaverModel?> = configureRepository.observeScreensaver()
}
