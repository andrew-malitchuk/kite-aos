package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.AutoRebootModel
import kotlinx.coroutines.flow.Flow

public interface ObserveAutoRebootUseCase {
    public operator fun invoke(): Flow<AutoRebootModel?>
}
