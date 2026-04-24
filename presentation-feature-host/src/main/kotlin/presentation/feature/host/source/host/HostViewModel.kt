package presentation.feature.host.source.host

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.core.core.execute.executeResult
import domain.core.source.model.ThemeModel
import domain.usecase.api.source.usecase.configuration.GetOnboardingStatusUseCase
import domain.usecase.api.source.usecase.configuration.ObserveThemeUseCase
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import presentation.core.navigation.api.source.destination.Destination

/**
 * ViewModel for the [HostActivity].
 *
 * Manages the application bootstrapping logic, including determining the initial
 * navigation route and observing the global theme settings.
 *
 * @param getOnboardingStatusUseCase Use case to check if onboarding has been completed.
 * @param observeThemeUseCase Use case to observe real-time changes to the application theme.
 * @see HostActivity
 * @see HostState
 * @see HostSideEffect
 * @see HostIntent
 * @since 0.0.1
 */
@KoinViewModel
public class HostViewModel(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val observeThemeUseCase: ObserveThemeUseCase,
) : ContainerHost<HostState, HostSideEffect>, ViewModel() {
    override val container: Container<HostState, HostSideEffect> = container(HostState())

    init {
        determineStartDestination()
        observeTheme()
    }

    /**
     * Subscribes to theme updates and updates the UI state accordingly.
     */
    private fun observeTheme() = intent {
        observeThemeUseCase().collect { result ->
            val theme = result.getOrNull() ?: ThemeModel.Light
            reduce { state.copy(theme = theme) }
        }
    }

    /**
     * Checks the onboarding status to decide which screen to show first.
     * Triggers the [HostSideEffect.DismissSplashEffect] once the decision is made.
     */
    private fun determineStartDestination() = executeResult(
        scope = viewModelScope,
        request = { getOnboardingStatusUseCase() },
        result = { isOnboardingCompleted ->
            intent {
                val startDestination =
                    if (isOnboardingCompleted == true) {
                        Destination.Main
                    } else {
                        Destination.Onboarding
                    }
                reduce { state.copy(startDestination = startDestination) }
                postSideEffect(HostSideEffect.DismissSplashEffect)
            }
        },
        errorBlock = {
            intent {
                reduce { state.copy(startDestination = Destination.Onboarding) }
                postSideEffect(HostSideEffect.DismissSplashEffect)
            }
        },
    )
}
