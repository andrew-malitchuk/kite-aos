package domain.usecase.impl.source.usecase.configuration

import domain.core.source.model.AutoRebootModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.ObserveAutoRebootUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [ObserveAutoRebootUseCase::class])
internal class ObserveAutoRebootUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveAutoRebootUseCase {
    override fun invoke(): Flow<AutoRebootModel?> = configureRepository.observeAutoReboot()
}
