package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing the physical position of the control dock (sidebar).
 *
 * @property position The selected dock position.
 */
public data class DockPositionModel(
    val position: Position
) : Model {
    /**
     * Supported dock positions.
     */
    public enum class Position(public val position: String) {
        /** Dock positioned at the top of the screen. */
        Up("up"),
        /** Dock positioned at the left side of the screen. */
        Left("left")
    }
}
