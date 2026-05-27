package domain.usecase.api.source.usecase.configuration

import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time network connectivity changes.
 *
 * Emits `true` when a network with internet capability becomes available
 * and `false` when it is lost.
 *
 * @since 0.0.5
 */
public interface ObserveNetworkStatusUseCase {
    /**
     * @return A [Flow] that emits `true` when the network is available and `false` when lost.
     *         The first emission reflects the current connectivity state at subscription time.
     */
    public operator fun invoke(): Flow<Boolean>
}
