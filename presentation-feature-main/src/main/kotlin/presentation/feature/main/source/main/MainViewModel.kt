package presentation.feature.main.source.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.core.core.execute.executeResult
import domain.core.core.monad.Failure
import domain.core.source.model.ApplicationModel
import domain.core.source.model.DashboardModel
import domain.core.source.model.DockPositionModel
import domain.core.source.model.MoveDetectorModel
import domain.usecase.api.source.usecase.application.LoadApplicationsUseCase
import domain.usecase.api.source.usecase.configuration.GetDashboardUseCase
import domain.usecase.api.source.usecase.device.GetDockPositionUseCase
import domain.usecase.api.source.usecase.device.GetMoveDetectorUseCase
import domain.usecase.api.source.usecase.device.ObserveMoveDetectorMotionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import presentation.core.localisation.R

/**
 * ViewModel for the Main (Kiosk) screen.
 *
 * It orchestrates the dashboard URL loading, manages the control drawer visibility,
 * and reacts to motion detection events to show/hide navigation controls.
 */
@KoinViewModel
public class MainViewModel(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val loadApplicationsUseCase: LoadApplicationsUseCase,
    private val getDockPositionUseCase: GetDockPositionUseCase,
    private val getMoveDetectorUseCase: GetMoveDetectorUseCase,
    private val observeMoveDetectorMotionUseCase: ObserveMoveDetectorMotionUseCase,
) : ContainerHost<MainState, MainSideEffect>,
    ViewModel() {
    public override val container: Container<MainState, MainSideEffect> =
        container(MainState())

    private fun mapError(failure: Throwable): Int = when (failure) {
        is Failure.Technical.Network -> R.string.error_network
        is Failure.Technical.Database -> R.string.error_database
        is Failure.Technical.Preference -> R.string.error_preference
        is Failure.Logic.NotFound -> R.string.error_not_found
        is Failure.Logic.Business -> R.string.error_unknown
        else -> R.string.error_unknown
    }

    private var fabTimerJob: Job? = null

    init {
        observeMotion()
    }

    private fun observeMotion() = intent {
        observeMoveDetectorMotionUseCase().collect {
            if (state.isMoveDetectorEnabled) {
                onMotionDetected()
            }
        }
    }

    @Suppress("MagicNumber")
    private fun onMotionDetected() = intent {
        reduce { state.copy(isFabVisible = true) }

        fabTimerJob?.cancel()
        fabTimerJob =
            intent {
                delay(state.fabDelay * 1000)
                reduce { state.copy(isFabVisible = false) }
            }
    }

    private fun onLoad(): Job = executeResult(
        scope = viewModelScope,
        request = {
            val dashboard = getDashboardUseCase().getOrNull()
            val apps = loadApplicationsUseCase(chosen = true).getOrDefault(emptyList())
            val dock = getDockPositionUseCase().getOrNull()
            val moveDetector = getMoveDetectorUseCase().getOrNull()

            Result.success(
                MainLoadResult(
                    dashboard = dashboard,
                    apps = apps,
                    dock = dock,
                    moveDetector = moveDetector,
                ),
            )
        },
        result = { result ->
            intent {
                reduce {
                    state.copy(
                        dashboardUrls = result?.dashboard,
                        chosenApps = result?.apps ?: emptyList(),
                        dockPosition = result?.dock,
                        isMoveDetectorEnabled = result?.moveDetector?.enabled ?: false,
                        fabDelay = result?.moveDetector?.fabDelay ?: 60L,
                    )
                }
                if (state.isMoveDetectorEnabled) {
                    onMotionDetected()
                }
            }
        },
        errorBlock = { failure ->
            intent {
                postSideEffect(MainSideEffect.ShowError(mapError(failure)))
            }
        },
    )

    public fun onGoToSettings(): Job = intent {
        postSideEffect(MainSideEffect.GoToSettingsEffect)
    }

    public fun onOpenApplication(packageName: String): Job = intent {
        postSideEffect(MainSideEffect.OpenApplicationEffect(packageName))
    }

    /**
     * Entry point for UI intents.
     */
    public fun handleIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.OnLoadIntent -> onLoad()
            MainIntent.OnReloadIntent -> onLoad()
            MainIntent.OnSettingsClickAction -> onGoToSettings()
            is MainIntent.OnOpenApplicationIntent -> onOpenApplication(intent.packageName)
        }
    }

    private data class MainLoadResult(
        val dashboard: DashboardModel?,
        val apps: List<ApplicationModel>,
        val dock: DockPositionModel?,
        val moveDetector: MoveDetectorModel?,
    )
}
