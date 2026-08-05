package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing which camera the motion-detection / streaming pipeline should use.
 *
 * The value is a user preference that overrides the automatic, capability-based source selection
 * performed by the platform layer:
 * - [Auto] keeps the legacy behaviour — the highest-priority available source wins (CameraX front
 *   on mobile; Camera2-external / UVC / MQTT on TV).
 * - [Front] / [Rear] force the built-in CameraX camera with the matching lens.
 * - [External] forces an attached USB/UVC webcam (Camera2 external, falling back to the AUSBC
 *   engine). When the forced choice is unavailable at runtime the platform falls back to [Auto].
 *
 * @property mode The stable string identifier persisted in Proto DataStore.
 * @see Model
 * @since 1.4.0
 */
public enum class CameraSourceModel(public val mode: String) : Model {
    /** Automatic capability-based selection (default). */
    Auto("auto"),

    /** Built-in front-facing camera. */
    Front("front"),

    /** Built-in rear-facing camera. */
    Rear("rear"),

    /** Attached external USB / UVC webcam. */
    External("external"),
}
