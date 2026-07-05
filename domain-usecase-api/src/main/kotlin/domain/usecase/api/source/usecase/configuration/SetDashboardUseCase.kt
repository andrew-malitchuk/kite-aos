package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.DashboardModel
import domain.usecase.api.source.common.Optional

/**
 * Use case for updating the kiosk dashboard configuration.
 *
 * @see GetDashboardUseCase
 * @see DashboardModel
 * @since 0.0.1
 */
public interface SetDashboardUseCase {
    /**
     * Writes the given dashboard configuration to persistent storage.
     *
     * @param dashboardModel The new dashboard URL and URL-whitelist configuration to persist.
     * @return `Result.success(Unit)` on success, or `Result.failure` with a `Failure`
     *   if the write operation fails.
     */
    public suspend operator fun invoke(dashboardModel: DashboardModel): Optional
}
