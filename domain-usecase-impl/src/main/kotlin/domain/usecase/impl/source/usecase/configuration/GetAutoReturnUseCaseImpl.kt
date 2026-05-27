package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.GetAutoReturnUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [GetAutoReturnUseCase] using [ConfigureRepository].
 *
 * @see GetAutoReturnUseCase
 * @since 0.0.4
 */
@Single(binds = [GetAutoReturnUseCase::class])
internal class GetAutoReturnUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : GetAutoReturnUseCase {
    override suspend fun invoke(): Result<Boolean> = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.getAutoReturn() ?: false
    }
}
