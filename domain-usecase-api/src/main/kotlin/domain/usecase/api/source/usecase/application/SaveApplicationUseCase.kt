package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel
import domain.usecase.api.core.common.Optional

/**
 * Use case for saving an application to the user's selected list.
 *
 * @see domain.usecase.impl.source.usecase.application.SaveApplicationUseCaseImpl
 * @since 0.0.1
 */
public interface SaveApplicationUseCase {
    /**
     * Persists the specified [applicationModel] to the local database.
     *
     * @param applicationModel The application to save.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(applicationModel: ApplicationModel): Optional
}
