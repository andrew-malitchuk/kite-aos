package domain.usecase.impl.source.usecase.configuration

import domain.core.source.monad.Failure
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.common.Optional
import domain.usecase.api.source.usecase.configuration.SetAutoReturnUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SetAutoReturnUseCase] using [ConfigureRepository].
 *
 * @see SetAutoReturnUseCase
 * @since 0.0.4
 */
@Single(binds = [SetAutoReturnUseCase::class])
internal class SetAutoReturnUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : SetAutoReturnUseCase {
    override suspend fun invoke(value: Boolean): Optional = resultLauncher(
        errorMapper = Failure.Technical::Preference,
    ) {
        configureRepository.setAutoReturn(value)
    }
}
