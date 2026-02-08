package presentation.core.styling.core

import androidx.compose.ui.unit.Dp

/**
 * Defines spacing tokens used for margins, paddings, and gaps between elements.
 * Maintains vertical and horizontal rhythm across the application using a standardized scale.
 */
public data class ThemeSpacing(
    //region small range
    /** Minimal spacing. */
    val sizeXXS: Dp,
    /** Very small spacing. */
    val sizeXS: Dp,
    //endregion small range

    //region medium range
    /** Small spacing. */
    val sizeS: Dp,
    /** Standard medium spacing. */
    val sizeM: Dp,
    /** Standard large spacing, common for outer container paddings. */
    val sizeL: Dp,
    //endregion medium range

    //region large range
    /** Extra large spacing. */
    val sizeXL: Dp,
    /** Double extra-large spacing. */
    val size2XL: Dp,
    /** Triple extra-large spacing. */
    val size3XL: Dp,
    /** Quadruple extra-large spacing. */
    val size4XL: Dp,
    /** Quintuple extra-large spacing. */
    val size5XL: Dp
    //region large range
)
