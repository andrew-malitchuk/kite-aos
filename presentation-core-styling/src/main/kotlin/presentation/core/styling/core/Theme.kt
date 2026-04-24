package presentation.core.styling.core

import androidx.compose.runtime.Composable
import presentation.core.styling.source.provider.LocalThemeColor
import presentation.core.styling.source.provider.LocalThemeFontSize
import presentation.core.styling.source.provider.LocalThemeLineHeight
import presentation.core.styling.source.provider.LocalThemeSize
import presentation.core.styling.source.provider.LocalThemeSpacing
import presentation.core.styling.source.provider.LocalThemeTypography

/**
 * Central access point for the application's theme attributes.
 *
 * This object provides a convenient way to access the current values for colors,
 * typography, spacing, and sizing from any Composable in the UI tree.
 * It uses CompositionLocals under the hood to retrieve the values provided by
 * [presentation.core.styling.source.theme.AppTheme].
 *
 * Usage:
 * ```kotlin
 * Text(
 *     text = "Hello",
 *     color = Theme.color.inkMain,
 *     style = Theme.typography.body,
 * )
 * ```
 *
 * @see ThemeColor
 * @see ThemeFontSize
 * @see ThemeSize
 * @see ThemeLineHeight
 * @see ThemeSpacing
 * @see ThemeTypography
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
public object Theme {
    /**
     * The current color palette.
     *
     * Provides access to all semantic color tokens (brand, surface, ink, outline, status,
     * and interaction colors) for the active theme.
     *
     * @see ThemeColor
     * @since 0.0.1
     */
    public val color: ThemeColor
        @Composable
        get() = LocalThemeColor.current

    /**
     * The current font size configuration.
     *
     * Provides access to text size tokens mapped to functional roles such as
     * display, title, body, caption, and action.
     *
     * @see ThemeFontSize
     * @since 0.0.1
     */
    public val fontSize: ThemeFontSize
        @Composable
        get() = LocalThemeFontSize.current

    /**
     * The current structural sizing tokens.
     *
     * Provides access to a T-shirt sizing scale (XXS to 5XL) used for icons,
     * avatars, containers, and other structural elements.
     *
     * @see ThemeSize
     * @since 0.0.1
     */
    public val size: ThemeSize
        @Composable
        get() = LocalThemeSize.current

    /**
     * The current line height configuration.
     *
     * Provides access to line height values that pair with font sizes to maintain
     * consistent vertical rhythm across all typography styles.
     *
     * @see ThemeLineHeight
     * @since 0.0.1
     */
    public val lineHeight: ThemeLineHeight
        @Composable
        get() = LocalThemeLineHeight.current

    /**
     * The current spacing tokens (margins, paddings, and gaps).
     *
     * Provides access to a standardized spacing scale used for layout rhythm
     * and structural alignment throughout the application.
     *
     * @see ThemeSpacing
     * @since 0.0.1
     */
    public val spacing: ThemeSpacing
        @Composable
        get() = LocalThemeSpacing.current

    /**
     * The current typography styles.
     *
     * Provides access to fully composed [androidx.compose.ui.text.TextStyle] instances
     * combining font family, size, weight, and line height for each semantic role.
     *
     * @see ThemeTypography
     * @since 0.0.1
     */
    public val typography: ThemeTypography
        @Composable
        get() = LocalThemeTypography.current
}
