package domain.usecase.api.source.usecase.application

import domain.core.source.model.ApplicationModel

/**
 * Use case for retrieving details of a specific application by its package name.
 *
 * @see domain.usecase.impl.source.usecase.application.GetApplicationUseCaseImpl
 * @since 0.0.1
 */
public interface GetApplicationUseCase {
    /**
     * Queries the platform package manager for the application matching the given package name.
     *
     * @param packageName The fully-qualified Android package identifier (e.g., `"com.example.app"`).
     * @return `Result.success` wrapping the matching [ApplicationModel], or `Result.failure`
     *   with a `Failure.Logic.NotFound` if no installed app matches the package name, or a
     *   `Failure.Technical.Platform` if the platform query fails.
     */
    public suspend operator fun invoke(packageName: String): Result<ApplicationModel>
}
