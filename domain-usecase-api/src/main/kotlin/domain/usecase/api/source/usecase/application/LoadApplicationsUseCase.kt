package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel

/**
 * Use case for loading applications, optionally merging system-installed apps with user-saved preferences.
 *
 * @see domain.usecase.impl.source.usecase.application.LoadApplicationsUseCaseImpl
 * @since 0.0.1
 */
public interface LoadApplicationsUseCase {
    /**
     * Loads applications by merging the system package list with user-saved selections.
     *
     * @param chosen When `true`, returns only applications explicitly saved by the user via
     *   [SaveApplicationUseCase]. When `false`, returns all launcher-enabled applications
     *   with their saved-selection state reflected in each [ApplicationModel].
     * @return `Result.success` wrapping the filtered list of [ApplicationModel]s (may be empty),
     *   or `Result.failure` with a `Failure` if either the database or the platform query fails.
     */
    public suspend operator fun invoke(chosen: Boolean = false): Result<List<ApplicationModel>>
}
