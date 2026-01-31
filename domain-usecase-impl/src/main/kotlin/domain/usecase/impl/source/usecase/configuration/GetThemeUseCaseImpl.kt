package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.core.source.model.ThemeModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetThemeUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetThemeUseCase] using [ConfigureRepository].
 */
@Single(binds = [GetThemeUseCase::class])
internal class GetThemeUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : GetThemeUseCase {
    override suspend fun invoke(): Result<ThemeModel> = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        configureRepository.getTheme() ?: throw Failure.Logic.NotFound
    }
}