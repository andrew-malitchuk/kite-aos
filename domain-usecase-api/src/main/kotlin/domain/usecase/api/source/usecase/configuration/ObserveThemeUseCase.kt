package domain.usecase.api.source.usecase.configuration

import domain.core.source.model.ThemeModel
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time changes to the application theme.
 *
 * @see GetThemeUseCase
 * @see SetThemeUseCase
 * @see ThemeModel
 * @since 0.0.1
 */
public interface ObserveThemeUseCase {
    /**
     * Returns a cold [Flow] backed by the preference data store that re-emits whenever
     * the theme is updated via [SetThemeUseCase].
     *
     * Each emission is wrapped in a `Result` so that read errors surface to the collector
     * without terminating the flow. Emits `Result.success(null)` if no theme has been saved.
     * The flow never completes under normal operation — it terminates only when the
     * collector's coroutine scope is cancelled.
     *
     * @return A [Flow] of `Result<ThemeModel?>` representing the current theme state.
     */
    public operator fun invoke(): Flow<Result<ThemeModel?>>
}
