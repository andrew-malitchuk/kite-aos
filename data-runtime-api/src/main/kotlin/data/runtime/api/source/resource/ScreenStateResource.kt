package data.runtime.api.source.resource

import data.core.source.resource.Resource

/**
 * Sealed hierarchy representing the possible runtime display states of the kiosk device.
 *
 * This resource is used by the runtime data layer to communicate the current screen mode
 * between background services (e.g., motion detection, MQTT) and the UI layer without
 * persisting the state to disk.
 *
 * @see data.runtime.api.source.datasource.ScreenStateSource
 * @since 1.1.0
 */
public sealed class ScreenStateResource : Resource {

    /**
     * The screen is active and displaying the main kiosk dashboard.
     *
     * This is the default initial state emitted when the source is first created.
     */
    public data object Active : ScreenStateResource()

    /**
     * The screensaver overlay is currently active, covering the main dashboard.
     *
     * Typically triggered after a period of inactivity detected by the motion sensor.
     */
    public data object Screensaver : ScreenStateResource()
}
