package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.core.source.model.ThemeModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.configuration.SetThemeUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetThemeUseCase] using [ConfigureRepository].
 */
@Single(binds = [SetThemeUseCase::class])
internal class SetThemeUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetThemeUseCase {
    override suspend fun invoke(value: ThemeModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setTheme(value)
    }
}
