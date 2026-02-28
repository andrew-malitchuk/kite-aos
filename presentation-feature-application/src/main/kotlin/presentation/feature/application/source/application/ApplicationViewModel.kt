package presentation.feature.application.source.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.core.source.model.ApplicationModel
import domain.usecase.api.source.usecase.application.LoadApplicationsUseCase
import domain.usecase.api.source.usecase.application.RemoveApplicationUseCase
import domain.usecase.api.source.usecase.application.SaveApplicationUseCase
import kotlinx.coroutines.CancellationException
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import common.core.core.execute.executeResult
import domain.core.core.monad.Failure
import presentation.core.localisation.R

/**
 * ViewModel for the Application selection screen.
 *
 * It manages the loading, saving, and removing of "chosen" applications
 * that will be available in the kiosk's sidebar.
 */
@KoinViewModel
public class ApplicationViewModel(
    private val loadApplicationsUseCase: LoadApplicationsUseCase,
    private val saveApplicationUseCase: SaveApplicationUseCase,
    private val removeApplicationUseCase: RemoveApplicationUseCase
) : ContainerHost<ApplicationState, ApplicationSideEffect>, ViewModel() {

    override val container: Container<ApplicationState, ApplicationSideEffect> =
        container(ApplicationState()) {
            loadApplications()
        }

    private fun mapError(failure: Throwable): Int =
        when (failure) {
            is Failure.Technical.Network -> R.string.error_network
            is Failure.Technical.Database -> R.string.error_database
            is Failure.Technical.Preference -> R.string.error_preference
            is Failure.Logic.NotFound -> R.string.error_not_found
            is Failure.Logic.Business -> R.string.error_unknown
            else -> R.string.error_unknown
        }

    private fun handleError(failure: Throwable) {
        if (failure is CancellationException) return
        intent {
            postSideEffect(ApplicationSideEffect.ShowError(mapError(failure)))
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(ApplicationSideEffect.BackClick)
    }

    private fun loadApplications() = intent {
        performLoad()
    }

    private fun performLoad() = executeResult(
        scope = viewModelScope,
        request = { loadApplicationsUseCase() },
        result = { apps ->
            intent {
                val sortedApps = apps?.sortedByDescending { it.chosen == true }?:emptyList()

                reduce { state.copy(isLoading = false, data = sortedApps) }
            }
        },
        errorBlock = { failure ->
            intent {
                reduce { state.copy(isLoading = false, isError = true) }
            }
            handleError(failure)
        }
    )

    private fun saveApplication(applicationModel: ApplicationModel) = executeResult(
        scope = viewModelScope,
        request = {
            saveApplicationUseCase(applicationModel.copy(chosen = true))
        },
        result = {
            performLoad()
        },
        errorBlock = { handleError(it) }
    )

    private fun removeApplication(applicationModel: ApplicationModel) = executeResult(
        scope = viewModelScope,
        request = {
            removeApplicationUseCase(applicationModel)
        },
        result = {
            performLoad()
        },
        errorBlock = { handleError(it) }
    )

    /**
     * Entry point for user actions from the UI.
     */
    public fun handleIntent(intent: ApplicationIntent) {
        when (intent) {
            ApplicationIntent.OnBackClick -> onBackClick()
            ApplicationIntent.LoadApplications -> loadApplications()
            is ApplicationIntent.SaveApplication -> saveApplication(intent.applicationModel)
            is ApplicationIntent.RemoveApplication -> removeApplication(intent.applicationModel)
        }
    }
}
