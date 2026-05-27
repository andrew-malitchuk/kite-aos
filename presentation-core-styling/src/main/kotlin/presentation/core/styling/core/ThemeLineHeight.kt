package presentation.core.styling.core

import androidx.compose.ui.unit.TextUnit

/**
 * Defines the line heights for each typography style to maintain vertical rhythm.
 *
 * Each property pairs with the corresponding [ThemeFontSize] token to form a
 * cohesive text style when combined in [ThemeTypography].
 *
 * @property display Line height for display text.
 * @property title Line height for titles.
 * @property label Line height for labels.
 * @property body Line height for body text.
 * @property caption Line height for captions.
 * @property action Line height for buttons and interactive elements.
 * @see ThemeFontSize
 * @see ThemeTypography
 * @see Theme.lineHeight
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
public data class ThemeLineHeight(
    /**
     * Line height for display text.
     *
     * @since 0.0.1
     */
    val display: TextUnit,
    /**
     * Line height for titles.
     *
     * @since 0.0.1
     */
    val title: TextUnit,
    /**
     * Line height for labels.
     *
     * @since 0.0.1
     */
    val label: TextUnit,
    /**
     * Line height for body text.
     *
     * @since 0.0.1
     */
    val body: TextUnit,
    /**
     * Line height for captions.
     *
     * @since 0.0.1
     */
    val caption: TextUnit,
    /**
     * Line height for buttons and interactive elements.
     *
     * @since 0.0.1
     */
    val action: TextUnit,
)
