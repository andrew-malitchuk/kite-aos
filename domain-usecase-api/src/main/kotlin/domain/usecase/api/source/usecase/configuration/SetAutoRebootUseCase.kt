package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.AutoRebootModel
import domain.usecase.api.source.common.Optional

public interface SetAutoRebootUseCase {
    public suspend operator fun invoke(model: AutoRebootModel?): Optional
}
