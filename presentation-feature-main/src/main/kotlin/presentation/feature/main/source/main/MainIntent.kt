package presentation.feature.main.source.main

/**
 * User actions (intents) that can be performed on the Main screen.
 *
 * Intents are dispatched from [MainContent] and processed by [MainViewModel.handleIntent].
 *
 * @see MainViewModel
 * @see MainContent
 * @since 0.0.1
 */
public sealed class MainIntent {
    /** Initial load of kiosk configuration and state. */
    public data object OnLoadIntent : MainIntent()

    /** Manual reload of the kiosk dashboard. */
    public data object OnReloadIntent : MainIntent()

    /** User clicked the settings icon in the drawer. */
    public data object OnSettingsClickAction : MainIntent()

    /** User clicked an application icon in the drawer. */
    public data class OnOpenApplicationIntent(val packageName: String) : MainIntent()
}
