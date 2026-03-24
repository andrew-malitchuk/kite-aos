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
 * It uses CompositionLocals under the hood to retrieve the values provided by [AppTheme].
 */
public object Theme {
    /**
     * The current color palette.
     */
    public val color: ThemeColor
        @Composable
        get() = LocalThemeColor.current

    /**
     * The current font size configuration.
     */
    public val fontSize: ThemeFontSize
        @Composable
        get() = LocalThemeFontSize.current

    /**
     * The current structural sizing tokens.
     */
    public val size: ThemeSize
        @Composable
        get() = LocalThemeSize.current

    /**
     * The current line height configuration.
     */
    public val lineHeight: ThemeLineHeight
        @Composable
        get() = LocalThemeLineHeight.current

    /**
     * The current spacing tokens (margins, paddings).
     */
    public val spacing: ThemeSpacing
        @Composable
        get() = LocalThemeSpacing.current

    /**
     * The current typography styles.
     */
    public val typography: ThemeTypography
        @Composable
        get() = LocalThemeTypography.current
}
