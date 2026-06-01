package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.AutoRebootModel

public interface GetAutoRebootUseCase {
    public suspend operator fun invoke(): Result<AutoRebootModel>
}
