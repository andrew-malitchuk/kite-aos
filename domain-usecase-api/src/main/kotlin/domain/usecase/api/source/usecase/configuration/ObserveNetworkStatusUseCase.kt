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
     * Returns a [Flow] that continuously emits network availability changes.
     *
     * The first emission reflects the current connectivity state at the time of collection.
     * The flow never completes under normal operation — it terminates only when the
     * collector's coroutine scope is cancelled.
     *
     * @return A [Flow] that emits `true` when a network with internet capability becomes
     *   available, and `false` when it is lost.
     */
    public operator fun invoke(): Flow<Boolean>
}
