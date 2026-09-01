package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ResilienceModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing the unattended-operation safeguard settings.
 *
 * @see GetResilienceUseCase
 * @since 2.2.0
 */
public interface ObserveResilienceUseCase {
    /**
     * Returns a [Flow] emitting the settings on every change.
     *
     * @return a hot [Flow] of the current [ResilienceModel].
     */
    public operator fun invoke(): Flow<ResilienceModel?>
}
