package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel

/**
 * Use case for loading applications, optionally merging system-installed apps with user-saved preferences.
 */
public interface LoadApplicationsUseCase {
    /**
     * Loads a list of applications.
     *
     * @param chosen If `true`, only returns applications that have been selected by the user.
     * @return A [Result] containing the filtered list of [ApplicationModel]s.
     */
    public suspend operator fun invoke(chosen: Boolean = false): Result<List<ApplicationModel>>
}
