package domain.usecase.impl.source.usecase.application

import domain.core.core.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.repository.api.source.repository.ApplicationRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.application.RemoveApplicationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [RemoveApplicationUseCase] using [ApplicationRepository].
 */
@Single(binds = [RemoveApplicationUseCase::class])
internal class RemoveApplicationUseCaseImpl(
    private val applicationRepository: ApplicationRepository,
) : RemoveApplicationUseCase {
    override suspend fun invoke(applicationModel: ApplicationModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Database,
    ) {
        applicationRepository.removeApplication(applicationModel)
    }
}
