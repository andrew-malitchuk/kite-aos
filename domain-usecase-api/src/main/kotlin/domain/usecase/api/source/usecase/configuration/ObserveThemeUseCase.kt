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
     * @return A [Flow] emitting the current [ThemeModel] whenever it changes.
     */
    public operator fun invoke(): Flow<Result<ThemeModel?>>
}
