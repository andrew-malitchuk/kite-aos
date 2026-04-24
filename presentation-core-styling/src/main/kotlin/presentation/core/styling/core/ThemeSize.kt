package presentation.core.styling.core

import androidx.compose.ui.unit.Dp

/**
 * Defines structural sizing tokens used for UI elements like icons, avatars, and containers.
 *
 * Uses a T-shirt sizing convention (XXS to 5XL) for easy identification of relative scale.
 * These tokens are distinct from [ThemeSpacing] in that they describe the dimensions of
 * elements rather than the gaps between them.
 *
 * @property sizeXXS Extra-extra-small size.
 * @property sizeXS Extra-small size.
 * @property sizeS Small size.
 * @property sizeM Medium size.
 * @property sizeL Large size.
 * @property sizeXL Extra-large size.
 * @property size2XL Double extra-large size.
 * @property size3XL Triple extra-large size.
 * @property size4XL Quadruple extra-large size.
 * @property size5XL Quintuple extra-large size.
 * @see ThemeSpacing
 * @see Theme.size
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
public data class ThemeSize(
    //region small range
    /**
     * Extra-extra-small size.
     *
     * @since 0.0.1
     */
    val sizeXXS: Dp,
    /**
     * Extra-small size.
     *
     * @since 0.0.1
     */
    val sizeXS: Dp,
    //endregion small range
    //region medium range
    /**
     * Small size.
     *
     * @since 0.0.1
     */
    val sizeS: Dp,
    /**
     * Medium size.
     *
     * @since 0.0.1
     */
    val sizeM: Dp,
    /**
     * Large size.
     *
     * @since 0.0.1
     */
    val sizeL: Dp,
    //endregion medium range
    //region large range
    /**
     * Extra-large size.
     *
     * @since 0.0.1
     */
    val sizeXL: Dp,
    /**
     * Double extra-large size.
     *
     * @since 0.0.1
     */
    val size2XL: Dp,
    /**
     * Triple extra-large size.
     *
     * @since 0.0.1
     */
    val size3XL: Dp,
    /**
     * Quadruple extra-large size.
     *
     * @since 0.0.1
     */
    val size4XL: Dp,
    /**
     * Quintuple extra-large size.
     *
     * @since 0.0.1
     */
    val size5XL: Dp,
    //endregion large range
)
