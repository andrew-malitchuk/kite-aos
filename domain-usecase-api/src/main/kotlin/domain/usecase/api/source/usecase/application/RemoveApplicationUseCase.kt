package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel
import domain.usecase.api.core.common.Optional

/**
 * Use case for removing an application from the user's selected list.
 */
public interface RemoveApplicationUseCase {
    /**
     * Deletes the specified [applicationModel] from the persistent storage.
     *
     * @param applicationModel The application to remove.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(applicationModel: ApplicationModel): Optional
}
