package domain.usecase.impl.source.usecase.configuration

import domain.core.core.monad.Failure
import domain.core.source.model.ThemeModel
import domain.repository.api.source.repository.ConfigureRepository
import domain.usecase.api.source.usecase.configuration.ObserveThemeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Implementation of [ObserveThemeUseCase] using [ConfigureRepository].
 *
 * @see ObserveThemeUseCase
 * @since 0.0.1
 */
@Single(binds = [ObserveThemeUseCase::class])
internal class ObserveThemeUseCaseImpl(
    private val configureRepository: ConfigureRepository,
) : ObserveThemeUseCase {
    override fun invoke(): Flow<Result<ThemeModel?>> = configureRepository.observeTheme()
        // Map null theme emissions to NotFound failures, non-null to success
        .map { theme ->
            if (theme == null) {
                Result.failure(Failure.Logic.NotFound)
            } else {
                Result.success(theme)
            }
        }
        // Catch upstream exceptions and wrap them as Preference failures
        .catch { throwable ->
            emit(Result.failure(Failure.Technical.Preference(throwable)))
        }
}
