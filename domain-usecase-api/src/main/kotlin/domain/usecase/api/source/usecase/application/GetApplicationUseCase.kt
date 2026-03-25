package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel

/**
 * Use case for retrieving details of a specific application by its package name.
 */
public interface GetApplicationUseCase {
    /**
     * Retrieves the [ApplicationModel] for the specified [packageName].
     *
     * @param packageName The Android package name.
     * @return A [Result] containing the application details.
     */
    public suspend operator fun invoke(packageName: String): Result<ApplicationModel>
}
