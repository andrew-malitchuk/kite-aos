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
     * @return A [Result] containing the [DashboardModel].
     */
    public suspend operator fun invoke(): Result<DashboardModel>
}
