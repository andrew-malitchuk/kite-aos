package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for saving an application to the user's selected list.
 *
 * @see domain.usecase.impl.source.usecase.application.SaveApplicationUseCaseImpl
 * @since 0.0.1
 */
public interface SaveApplicationUseCase {
    /**
     * Inserts or updates the given application's record in the local database.
     *
     * @param applicationModel The application entry to add to the user's saved list.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a
     *   `Failure.Technical.Database` if the insert operation fails.
     */
    public suspend operator fun invoke(applicationModel: ApplicationModel): Optional
}
