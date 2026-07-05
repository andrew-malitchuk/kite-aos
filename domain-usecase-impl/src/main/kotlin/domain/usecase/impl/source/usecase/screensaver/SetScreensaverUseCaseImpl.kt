package domain.usecase.impl.source.usecase.screensaver

import domain.core.source.monad.Failure
import domain.core.source.model.ScreensaverModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.screensaver.SetScreensaverUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetScreensaverUseCase] using [ConfigureRepository].
 *
 * Persists the screensaver configuration; failures are mapped to
 * [domain.core.source.monad.Failure.Technical.Preference].
 *
 * @see SetScreensaverUseCase
 * @since 0.0.1
 */
@Single(binds = [SetScreensaverUseCase::class])
internal class SetScreensaverUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetScreensaverUseCase {
    override suspend fun invoke(screensaver: ScreensaverModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setScreensaver(screensaver)
    }
}
