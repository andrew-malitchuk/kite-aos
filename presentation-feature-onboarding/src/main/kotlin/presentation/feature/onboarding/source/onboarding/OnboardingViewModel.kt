package presentation.feature.onboarding.source.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.core.source.model.DashboardModel
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.SetDashboardUseCase
import domain.usecase.api.source.usecase.configuration.SetOnboardingStatusUseCase
import kotlinx.coroutines.Job
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import common.core.core.execute.executeResult
import domain.core.core.monad.Failure
import presentation.core.localisation.R

/**
 * ViewModel responsible for orchestrating the setup wizard and permission handling.
 *
 * It uses the [executeResult] utility to interact with domain use cases for reading/writing
 * configuration and onboarding status. It tracks the real-time status of various system
 * permissions and enables/disables navigation in the wizard accordingly.
 */
@KoinViewModel
public class OnboardingViewModel(
    private val setOnboardingStatusUseCase: SetOnboardingStatusUseCase,
    private val setDashboardUseCase: SetDashboardUseCase,
    private val getDashboardUseCase: GetDashboardUseCase
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
                dashboardUrls = null
            )
        )

    private fun mapError(failure: Throwable): Int =
        when (failure) {
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

    public fun askCameraPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskCameraPermissionEffect)
    }

    public fun askOverlayPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskOverlayPermissionEffect)
    }

    public fun askPostNotificationPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskPostNotificationPermissionEffect)
    }

    public fun askDeviceAdminPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskDeviceAdminEffect)
    }

    public fun askWriteSettingsPermission(): Job = intent {
        postSideEffect(OnboardingSideEffect.AskWriteSettingsEffect)
    }

    public fun onGoToMain(): Job = intent {
        postSideEffect(OnboardingSideEffect.GoToMainEffect)
    }

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
        }
    )


    public fun onCameraPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isCameraPermissionGranted = granted
            )
        }
    }

    public fun onOverlayPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isOverlayPermissionGranted = granted
            )
        }
    }

    public fun onPostNotificationPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isPostNotificationPermissionGranted = granted
            )
        }
    }

    public fun onDeviceAdminPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isDeviceAdminGranted = granted
            )
        }
    }

    public fun onWriteSettingsPermission(granted: Boolean): Job = intent {
        reduce {
            state.copy(
                isWriteSettingsGranted = granted
            )
        }
    }


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