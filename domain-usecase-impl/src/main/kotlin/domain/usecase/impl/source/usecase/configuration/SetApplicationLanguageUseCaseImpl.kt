package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.configuration.SetApplicationLanguageUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetApplicationLanguageUseCase] using [ConfigureRepository].
 */
@Single(binds = [SetApplicationLanguageUseCase::class])
internal class SetApplicationLanguageUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : SetApplicationLanguageUseCase {
    override suspend fun invoke(localeCode: String): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        configureRepository.setApplicationLanguage(localeCode)
    }
}
