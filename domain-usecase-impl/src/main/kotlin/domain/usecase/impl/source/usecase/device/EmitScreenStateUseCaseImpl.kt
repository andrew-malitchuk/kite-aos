package domain.usecase.impl.source.usecase.device

import domain.core.source.model.ScreenStateModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.device.EmitScreenStateUseCase
import org.koin.core.annotation.Single

@Single(binds = [EmitScreenStateUseCase::class])
internal class EmitScreenStateUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : EmitScreenStateUseCase {
    override suspend fun invoke(state: ScreenStateModel) {
        configureRepository.emitScreenState(state)
    }
}
