package presentation.feature.onboarding.source.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.core.core.execute.executeResult
import domain.core.core.monad.Failure
import domain.core.source.model.DashboardModel
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.SetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.SetOnboardingStatusUseCase
import kotlinx.coroutines.Job
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import presentation.core.localisation.R

/**
 * ViewModel responsible for orchestrating the setup wizard and permission handling.
 *
 * It uses the [executeResult] utility to interact with domain use cases for reading/writing
 * configuration and onboarding status. It tracks the real-time status of various system
 * permissions and enables/disables navigation in the wizard accordingly.
 *
 * @param setOnboardingStatusUseCase Use case to persist the onboarding completion flag.
 * @param setDashboardUseCase Use case to persist the dashboard and whitelist URLs.
 * @param getDashboardUseCase Use case to retrieve previously saved dashboard URLs.
 * @see OnboardingScreen
 * @see OnboardingState
 * @see OnboardingSideEffect
 * @see OnboardingIntent
 * @since 0.0.1
 */
@KoinViewModel
public class OnboardingViewModel(
    private val setOnboardingStatusUseCase: SetOnboardingStatusUseCase,
    private val setDashboardUseCase: SetDashboardUseCase,
    private val getDashboardUseCase: GetDashboardUseCase,
) : ContainerHost<OnboardingState, OnboardingSideEffect>,
    ViewModel() {
    public override val container: Container<OnboardingState, OnboardingSideEffect> =
        container(
            OnboardingState(
                isCameraPermissionGranted = false,
                isOverlayPermissionGranted = false,
                isPostNotificationPermissionGranted = false,
                isDeviceAdminGranted = false,
                isWriteSettingsGranted = false,
                dashboardUrls = null,
            ),
        )

    private fun mapError(failure: Throwable): Int = when (failure) {
        is Failure.Technical.Network -> R.string.error_network
        is Failure.Technical.Database -> R.string.error_database
        is Failure.Technical.Preference -> R.string.error_preference
        is Failure.Logic.NotFound -> R.string.error_not_found
        is Failure.Logic.Business -> R.string.error_unknown
        else -> R.string.error_unknown
    }

    init {
        loadDashboardUrls()
    }

    /**
     * Requests the camera permission by posting the corresponding side effect.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun askCameraPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskCameraPermissionEffect)
    }

    /**
     * Requests the overlay (draw-over-apps) permission by posting the corresponding side effect.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun askOverlayPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskOverlayPermissionEffect)
    }

    /**
     * Requests the POST_NOTIFICATIONS permission by posting the corresponding side effect.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun askPostNotificationPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskPostNotificationPermissionEffect)
    }

    /**
     * Requests the Device Administrator privilege by posting the corresponding side effect.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun askDeviceAdminPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskDeviceAdminEffect)
    }

    /**
     * Requests the WRITE_SETTINGS permission by posting the corresponding side effect.
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun askWriteSettingsPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskWriteSettingsEffect)
    }

    /**
     * Navigates to the main dashboard by posting the [OnboardingSideEffect.GoToMainEffect].
     *
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onGoToMain(): Job = intent {
        postSideEffect(OnboardingSideEffect.GoToMainEffect)
    }

    /**
     * Completes the onboarding flow by persisting the dashboard configuration
     * and marking onboarding as completed, then navigates to the main screen.
     *
     * @param dashboardUrl The Home Assistant dashboard URL entered by the user.
     * @param whitelistUrl The whitelist domain(s) for WebView navigation.
     * @return The [Job] associated with the use case execution.
     * @since 0.0.1
     */
    public fun onFinish(dashboardUrl: String, whitelistUrl: String): Job = executeResult(
        scope = viewModelScope,
        request = {
            setDashboardUseCase(DashboardModel(dashboardUrl, whitelistUrl))
            setOnboardingStatusUseCase(true)
        },
        result = {
            intent {
                postSideEffect(OnboardingSideEffect.GoToMainEffect)
            }
        },
        errorBlock = { failure ->
            intent {
                postSideEffect(OnboardingSideEffect.ShowError(mapError(failure)))
            }
        },
    )

    private fun loadDashboardUrls(): Job = executeResult(
        scope = viewModelScope,
        request = {
            getDashboardUseCase()
        },
        result = { dashboardModel ->
            intent {
                reduce {
                    state.copy(dashboardUrls = dashboardModel)
                }
            }
        },
        errorBlock = { failure ->
            intent {
                reduce {
                    state.copy(dashboardUrls = null)
                }
                postSideEffect(OnboardingSideEffect.ShowError(mapError(failure)))
            }
        },
    )

    /**
     * Updates the state with the camera permission result.
     *
     * @param granted Whether the camera permission was granted.
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onCameraPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isCameraPermissionGranted = granted,
            )
        }
    }

    /**
     * Updates the state with the overlay permission result.
     *
     * @param granted Whether the overlay permission was granted.
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onOverlayPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isOverlayPermissionGranted = granted,
            )
        }
    }

    /**
     * Updates the state with the POST_NOTIFICATIONS permission result.
     *
     * @param granted Whether the notification permission was granted.
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onPostNotificationPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isPostNotificationPermissionGranted = granted,
            )
        }
    }

    /**
     * Updates the state with the Device Administrator permission result.
     *
     * @param granted Whether the device admin privilege was granted.
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onDeviceAdminPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isDeviceAdminGranted = granted,
            )
        }
    }

    /**
     * Updates the state with the WRITE_SETTINGS permission result.
     *
     * @param granted Whether the write settings permission was granted.
     * @return The [Job] associated with the intent coroutine.
     * @since 0.0.1
     */
    public fun onWriteSettingsPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isWriteSettingsGranted = granted,
            )
        }
    }

    /**
     * Entry point for user actions from the UI.
     *
     * Routes each [OnboardingIntent] to the corresponding handler method.
     *
     * @param intent The user action to process.
     * @see OnboardingIntent
     * @since 0.0.1
     */
    public fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.OnAskCameraPermissionIntent -> askCameraPermission()
            is OnboardingIntent.OnAskOverlayPermissionIntent -> askOverlayPermission()
            is OnboardingIntent.OnAskPostNotificationPermissionIntent -> askPostNotificationPermission()
            is OnboardingIntent.OnAskDeviceAdminPermissionIntent -> askDeviceAdminPermission()
            is OnboardingIntent.OnAskWriteSettingsPermissionIntent -> askWriteSettingsPermission()
            is OnboardingIntent.GoToMainIntent -> onGoToMain()
            is OnboardingIntent.OnFinishIntent -> onFinish(intent.dashboardUrl, intent.whitelistUrl)
        }
    }
}
