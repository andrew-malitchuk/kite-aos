package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel

/**
 * Use case for retrieving all launcher-enabled applications installed on the system.
 *
 * @see domain.usecase.impl.source.usecase.application.GetApplicationsUseCaseImpl
 * @since 0.0.1
 */
public interface GetApplicationsUseCase {
    /**
     * Queries the platform package manager for all launcher-enabled applications.
     *
     * @return `Result.success` wrapping a list of [ApplicationModel]s (never null; may be empty
     *   if no launcher-enabled apps exist), or `Result.failure` with a `Failure` if the
     *   platform query fails.
     */
    public suspend operator fun invoke(): Result<List<ApplicationModel>>
}
