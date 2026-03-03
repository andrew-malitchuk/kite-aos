package presentation.feature.main.source.main

/**
 * User actions that can be performed on the Main screen.
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
