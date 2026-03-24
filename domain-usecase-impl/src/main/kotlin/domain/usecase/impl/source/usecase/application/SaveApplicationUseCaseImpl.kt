package domain.usecase.impl.source.usecase.application

import domain.core.core.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.repository.api.source.repository.ApplicationRepository
import domain.usecase.api.core.common.Optional
import domain.usecase.api.source.usecase.application.SaveApplicationUseCase
import domain.usecase.impl.core.resultLauncher
import org.koin.core.annotation.Single

/**
 * Implementation of [SaveApplicationUseCase] using [ApplicationRepository].
 */
@Single(binds = [SaveApplicationUseCase::class])
internal class SaveApplicationUseCaseImpl(
    private val applicationRepository: ApplicationRepository,
) : SaveApplicationUseCase {
    override suspend fun invoke(applicationModel: ApplicationModel): Optional = resultLauncher(
        errorMapper = Failure.Technical::Database,
    ) {
        applicationRepository.addApplication(applicationModel)
    }
}
