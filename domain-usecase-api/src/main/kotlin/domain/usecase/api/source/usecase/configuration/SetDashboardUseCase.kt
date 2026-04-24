package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.DashboardModel
import domain.usecase.api.core.common.Optional

/**
 * Use case for updating the kiosk dashboard configuration.
 *
 * @see GetDashboardUseCase
 * @see DashboardModel
 * @since 0.0.1
 */
public interface SetDashboardUseCase {
    /**
     * @param dashboardModel The new dashboard configuration to save.
     * @return An [Optional] result.
     */
    public suspend operator fun invoke(dashboardModel: DashboardModel): Optional
}
