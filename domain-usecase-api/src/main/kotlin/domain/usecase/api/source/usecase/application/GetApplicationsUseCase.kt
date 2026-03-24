package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel

/**
 * Use case for retrieving all launcher-enabled applications installed on the system.
 */
public interface GetApplicationsUseCase {
    /**
     * Retrieves a list of all available [ApplicationModel]s from the platform.
     *
     * @return A [Result] containing the list of installed applications.
     */
    public suspend operator fun invoke(): Result<List<ApplicationModel>>
}
