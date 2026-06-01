package domain.usecase.impl.source.usecase.screensaver

import domain.core.source.monad.Failure
import domain.core.source.model.ScreensaverModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.screensaver.SetScreensaverUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

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
