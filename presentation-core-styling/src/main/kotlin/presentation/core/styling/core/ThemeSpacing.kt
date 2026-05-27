package presentation.core.styling.core

import androidx.compose.ui.unit.Dp

/**
 * Defines spacing tokens used for margins, paddings, and gaps between elements.
 *
 * Maintains vertical and horizontal rhythm across the application using a standardized scale.
 * These tokens are distinct from [ThemeSize] in that they describe the whitespace between
 * elements rather than the dimensions of the elements themselves.
 *
 * @property sizeXXS Minimal spacing.
 * @property sizeXS Very small spacing.
 * @property sizeS Small spacing.
 * @property sizeM Standard medium spacing.
 * @property sizeL Standard large spacing, common for outer container paddings.
 * @property sizeXL Extra large spacing.
 * @property size2XL Double extra-large spacing.
 * @property size3XL Triple extra-large spacing.
 * @property size4XL Quadruple extra-large spacing.
 * @property size5XL Quintuple extra-large spacing.
 * @see ThemeSize
 * @see Theme.spacing
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
public data class ThemeSpacing(
    //region small range
    /**
     * Minimal spacing.
     *
     * @since 0.0.1
     */
    val sizeXXS: Dp,
    /**
     * Very small spacing.
     *
     * @since 0.0.1
     */
    val sizeXS: Dp,
    //endregion small range
    //region medium range
    /**
     * Small spacing.
     *
     * @since 0.0.1
     */
    val sizeS: Dp,
    /**
     * Standard medium spacing.
     *
     * @since 0.0.1
     */
    val sizeM: Dp,
    /**
     * Standard large spacing, common for outer container paddings.
     *
     * @since 0.0.1
     */
    val sizeL: Dp,
    //endregion medium range
    //region large range
    /**
     * Extra large spacing.
     *
     * @since 0.0.1
     */
    val sizeXL: Dp,
    /**
     * Double extra-large spacing.
     *
     * @since 0.0.1
     */
    val size2XL: Dp,
    /**
     * Triple extra-large spacing.
     *
     * @since 0.0.1
     */
    val size3XL: Dp,
    /**
     * Quadruple extra-large spacing.
     *
     * @since 0.0.1
     */
    val size4XL: Dp,
    /**
     * Quintuple extra-large spacing.
     *
     * @since 0.0.1
     */
    val size5XL: Dp,
    //endregion large range
)
