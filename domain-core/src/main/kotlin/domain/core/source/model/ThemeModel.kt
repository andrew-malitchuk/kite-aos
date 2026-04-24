package domain.core.source.model

import domain.core.source.model.base.Model

/**
 * Domain model representing the application's visual theme mode.
 *
 * @property mode The string identifier for the theme mode.
 *
 * @see Model
 * @since 0.0.1
 */
public enum class ThemeModel(public val mode: String) : Model {
    /** Classic light mode. */
    Light("light"),

    /** Classic dark mode. */
    Dark("dark"),

    /** Dynamic theme based on Android's Material You colors. */
    MaterialU("materialu"),
}
