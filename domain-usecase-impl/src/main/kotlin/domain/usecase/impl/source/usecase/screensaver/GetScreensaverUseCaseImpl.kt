package domain.usecase.impl.source.usecase.screensaver

import domain.core.source.monad.Failure
import domain.core.source.model.ScreensaverModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.screensaver.GetScreensaverUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetScreensaverUseCase] using [ConfigureRepository].
 *
 * Returns `null` inside the [Result] when no screensaver configuration has been stored yet,
 * allowing callers to distinguish between "not configured" and a preference read error.
 *
 * @see GetScreensaverUseCase
 * @since 0.0.1
 */
@Single(binds = [GetScreensaverUseCase::class])
internal class GetScreensaverUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetScreensaverUseCase {
    override suspend fun invoke(): Result<ScreensaverModel?> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getScreensaver()
    }
}
