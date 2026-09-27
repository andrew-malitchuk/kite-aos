package domain.usecase.api.source.usecase.mqtt

import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing inbound "clear cache" button presses from Home Assistant.
 *
 * Each emission is a request to flush the WebView cache and reload the dashboard — the one-click
 * recovery path for a panel wedged on stale assets.
 *
 * @since 2.1.0
 */
public interface ObserveMqttClearCacheCommandUseCase {
    /**
     * Returns a [Flow] that emits once per press of the Home Assistant `clear_cache` button.
     *
     * The payload carries no information — a Home Assistant `button` entity is a pure trigger —
     * so emissions are [Unit].
     *
     * The flow never completes under normal operation; it terminates only when the collector's
     * coroutine scope is cancelled.
     *
     * @return A hot [Flow] of [Unit] representing remote cache-flush requests.
     */
    public operator fun invoke(): Flow<Unit>
}
