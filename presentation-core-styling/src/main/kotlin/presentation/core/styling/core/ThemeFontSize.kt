package presentation.core.styling.core

import androidx.compose.ui.unit.TextUnit

/**
 * Defines the font sizes used throughout the application.
 *
 * Uses functional naming to map sizes to specific UI roles rather than
 * raw numeric identifiers. Each token corresponds to a semantic text role
 * within the design system.
 *
 * @property display Size for large display or hero text.
 * @property title Size for main screen titles.
 * @property label Size for small headers, card titles, or input labels.
 * @property body Size for standard readable body text.
 * @property caption Size for secondary information and captions.
 * @property action Size for interactive elements like buttons and links.
 * @see ThemeTypography
 * @see ThemeLineHeight
 * @see Theme.fontSize
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
public data class ThemeFontSize(
    /**
     * Size for large display or hero text.
     *
     * @since 0.0.1
     */
    val display: TextUnit,
    /**
     * Size for main screen titles.
     *
     * @since 0.0.1
     */
    val title: TextUnit,
    /**
     * Size for small headers, card titles, or input labels.
     *
     * @since 0.0.1
     */
    val label: TextUnit,
    /**
     * Size for standard readable body text.
     *
     * @since 0.0.1
     */
    val body: TextUnit,
    /**
     * Size for secondary information and captions.
     *
     * @since 0.0.1
     */
    val caption: TextUnit,
    /**
     * Size for interactive elements like buttons and links.
     *
     * @since 0.0.1
     */
    val action: TextUnit,
)
