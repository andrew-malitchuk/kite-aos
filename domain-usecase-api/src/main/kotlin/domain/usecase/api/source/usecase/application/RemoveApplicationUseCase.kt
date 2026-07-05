package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for removing an application from the user's selected list.
 *
 * @see domain.usecase.impl.source.usecase.application.RemoveApplicationUseCaseImpl
 * @since 0.0.1
 */
public interface RemoveApplicationUseCase {
    /**
     * Deletes the given application's saved record from the local database.
     *
     * @param applicationModel The application entry to remove from the user's saved list.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a
     *   `Failure.Technical.Database` if the delete operation fails.
     */
    public suspend operator fun invoke(applicationModel: ApplicationModel): Optional
}
