package presentation.core.styling.core

import androidx.compose.ui.text.TextStyle

/**
 * Combines font sizes, line heights, and other text properties into reusable styles.
 *
 * Each property is a fully composed [TextStyle] that includes font family, weight,
 * size, and line height. This class serves as the single source of truth for all
 * text styling in the application.
 *
 * @property display Prominent display style for hero sections.
 * @property title Main title style for screen headers.
 * @property label Style for sub-headers and labels.
 * @property body Standard style for long-form content.
 * @property bodyEmphasis High-emphasis version of the body style.
 * @property caption Style for small secondary text.
 * @property action Specialized style for buttons and actions.
 * @see ThemeFontSize
 * @see ThemeLineHeight
 * @see Theme.typography
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
public data class ThemeTypography(
    /**
     * Prominent display style for hero sections.
     *
     * @since 0.0.1
     */
    val display: TextStyle,
    /**
     * Main title style for screen headers.
     *
     * @since 0.0.1
     */
    val title: TextStyle,
    /**
     * Style for sub-headers and labels.
     *
     * @since 0.0.1
     */
    val label: TextStyle,
    /**
     * Standard style for long-form content.
     *
     * @since 0.0.1
     */
    val body: TextStyle,
    /**
     * High-emphasis version of the body style.
     *
     * @since 0.0.1
     */
    val bodyEmphasis: TextStyle,
    /**
     * Style for small secondary text.
     *
     * @since 0.0.1
     */
    val caption: TextStyle,
    /**
     * Specialized style for buttons and actions.
     *
     * @since 0.0.1
     */
    val action: TextStyle,
)
