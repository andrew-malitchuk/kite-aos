package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Sealed domain model representing the current display state of the kiosk screen.
 *
 * Observers of screen state should handle both subtypes to react appropriately
 * (e.g., pausing WebView refresh when screensaver is active).
 *
 * @see Model
 * @since 0.0.1
 */
public sealed class ScreenStateModel : Model {
    /** The screen is in the normal interactive kiosk view. */
    public data object Active : ScreenStateModel()

    /** The screensaver overlay is currently shown due to inactivity. */
    public data object Screensaver : ScreenStateModel()

    /**
     * A plain dark overlay is shown as a stand-in for powering the screen off, used on devices
     * where the app cannot turn the panel off (e.g. Android TV, where device locking is
     * unavailable). Unlike [Screensaver] it never shows images or a clock.
     */
    public data object DarkOverlay : ScreenStateModel()
}
