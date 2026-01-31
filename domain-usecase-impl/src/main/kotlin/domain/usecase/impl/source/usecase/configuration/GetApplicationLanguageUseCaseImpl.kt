package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetApplicationLanguageUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetApplicationLanguageUseCase] using [ConfigureRepository].
 */
@Single(binds = [GetApplicationLanguageUseCase::class])
internal class GetApplicationLanguageUseCaseImpl(
    private val configureRepository: ConfigureRepository
) : GetApplicationLanguageUseCase {
    override suspend fun invoke(): Result<String?> = resultLauncher(
        errorMapper = Failure.Technical::Preference
    ) {
        configureRepository.getApplicationLanguage()
    }
}
