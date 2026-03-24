package presentation.core.styling.core

import androidx.compose.ui.unit.Dp

/**
 * Defines structural sizing tokens used for UI elements like icons, avatars, and containers.
 * Uses a T-shirt sizing convention (XXS to 5XL) for easy identification of relative scale.
 */
public data class ThemeSize(
    //region small range
    /** Extra-extra-small size. */
    val sizeXXS: Dp,
    /** Extra-small size. */
    val sizeXS: Dp,
    //endregion small range
    //region medium range
    /** Small size. */
    val sizeS: Dp,
    /** Medium size. */
    val sizeM: Dp,
    /** Large size. */
    val sizeL: Dp,
    //endregion medium range
    //region large range
    /** Extra-large size. */
    val sizeXL: Dp,
    /** Double extra-large size. */
    val size2XL: Dp,
    /** Triple extra-large size. */
    val size3XL: Dp,
    /** Quadruple extra-large size. */
    val size4XL: Dp,
    /** Quintuple extra-large size. */
    val size5XL: Dp,
    //region large range
)
