package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.DashboardModel

/**
 * Use case for retrieving the kiosk dashboard configuration (URL and whitelist).
 *
 * @see SetDashboardUseCase
 * @see DashboardModel
 * @since 0.0.1
 */
public interface GetDashboardUseCase {
    /**
     * Reads the dashboard configuration from persistent storage.
     *
     * @return `Result.success` wrapping the current [DashboardModel] (URL and whitelist),
     *   or `Result.failure` with a `Failure` if the preference store is unavailable.
     */
    public suspend operator fun invoke(): Result<DashboardModel>
}
