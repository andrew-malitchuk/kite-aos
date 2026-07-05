# MVI Pattern

Kite uses [Orbit MVI](https://github.com/orbit-mvi/orbit-mvi) for all feature screen state management.

## Core Concepts

| Concept | Description |
|---------|-------------|
| **State** | Immutable snapshot of what the UI currently shows |
| **Intent** | User action or system event that may change state |
| **SideEffect** | One-shot event (navigate, show snackbar) — not stored in state |

The ViewModel implements `ContainerHost<State, SideEffect>` and processes intents via the `intent { }` block.

## Feature Module Structure

Every feature follows the same layout:

```
presentation-feature-{name}/
  source/{name}/
    {Name}Screen.kt      — Navigation entry point, collectAsState / collectSideEffect
    {Name}Content.kt     — Stateless @Composable, receives State + callbacks
    {Name}ViewModel.kt   — ContainerHost<State, SideEffect>, @KoinViewModel
    {Name}Contract.kt    — State data class + SideEffect sealed class
    {Name}Intent.kt      — Sealed class of user actions
  di/
    PresentationFeature{Name}Module.kt — @Module @ComponentScan
```

## Data Flow

```
User gesture
     │
     ▼
{Name}Screen.kt          — translates gesture to Intent
     │
     ▼
{Name}ViewModel.kt       — intent { reduce { state.copy(...) } }
     │                      intent { postSideEffect(...) }
     ▼
ContainerHost container
     │
     ├─ StateFlow<State> ──► {Name}Content.kt (recompose on change)
     └─ SideEffect ────────► {Name}Screen.kt (one-shot: navigate, toast)
```

## Code Pattern

### Contract

```kotlin
data class SettingsState(
    val theme: ThemeModel = ThemeModel.System,
    val isLoading: Boolean = false,
)

sealed class SettingsSideEffect {
    data object NavigateBack : SettingsSideEffect()
    data class ShowError(val message: String) : SettingsSideEffect()
}
```

### Intent

```kotlin
sealed class SettingsIntent {
    data class SetTheme(val theme: ThemeModel) : SettingsIntent()
    data object NavigateBack : SettingsIntent()
}
```

### ViewModel

```kotlin
@KoinViewModel
class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
) : ContainerHost<SettingsState, SettingsSideEffect>, ViewModel() {

    override val container = container<SettingsState, SettingsSideEffect>(SettingsState())

    fun onIntent(intent: SettingsIntent) = when (intent) {
        is SettingsIntent.SetTheme -> setTheme(intent.theme)
        SettingsIntent.NavigateBack -> intent { postSideEffect(SettingsSideEffect.NavigateBack) }
    }

    private fun setTheme(theme: ThemeModel) = intent {
        setThemeUseCase(theme)
            .onSuccess { reduce { state.copy(theme = theme) } }
            .onFailure { postSideEffect(SettingsSideEffect.ShowError(it.toString())) }
    }
}
```

### Screen (navigation entry point)

```kotlin
@Composable
fun SettingsScreen(navigator: AppNavigator) {
    val viewModel = koinViewModel<SettingsViewModel>()
    val state by viewModel.container.stateFlow.collectAsState()

    viewModel.container.sideEffectFlow.collectWithLifecycle { effect ->
        when (effect) {
            SettingsSideEffect.NavigateBack -> navigator.navigateBack()
            is SettingsSideEffect.ShowError -> { /* show snackbar */ }
        }
    }

    SettingsContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
```

### Content (stateless, testable)

```kotlin
@Composable
fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
) {
    // Pure UI — no ViewModel reference, no side effects
}
```

## Dependency Injection

ViewModels are injected via `@KoinViewModel` and declared in the feature's DI module:

```kotlin
@Module
@ComponentScan("com.yourpackage.feature.settings")
class PresentationFeatureSettingsModule
```

The module is included in `AppModule.kt` in `presentation-core-application`.
