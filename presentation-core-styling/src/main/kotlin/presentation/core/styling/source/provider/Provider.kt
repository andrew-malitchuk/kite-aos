package presentation.core.styling.source.provider

import androidx.compose.runtime.staticCompositionLocalOf
import presentation.core.styling.core.ThemeColor
import presentation.core.styling.core.ThemeFontSize
import presentation.core.styling.core.ThemeLineHeight
import presentation.core.styling.core.ThemeSize
import presentation.core.styling.core.ThemeSpacing
import presentation.core.styling.core.ThemeTypography

/**
 * CompositionLocal provider for the current [ThemeColor] palette.
 *
 * Throws an error if accessed outside of a properly configured theme scope
 * (i.e., without [presentation.core.styling.source.theme.AppTheme] in the composition tree).
 *
 * @see ThemeColor
 * @since 0.0.1
 */
internal val LocalThemeColor =
    staticCompositionLocalOf<ThemeColor> {
        error("No implementation")
    }

/**
 * CompositionLocal provider for the current [ThemeFontSize] configuration.
 *
 * Throws an error if accessed outside of a properly configured theme scope.
 *
 * @see ThemeFontSize
 * @since 0.0.1
 */
internal val LocalThemeFontSize =
    staticCompositionLocalOf<ThemeFontSize> {
        error("No implementation")
    }

/**
 * CompositionLocal provider for the current [ThemeSize] tokens.
 *
 * Throws an error if accessed outside of a properly configured theme scope.
 *
 * @see ThemeSize
 * @since 0.0.1
 */
internal val LocalThemeSize =
    staticCompositionLocalOf<ThemeSize> {
        error("No implementation")
    }

/**
 * CompositionLocal provider for the current [ThemeLineHeight] configuration.
 *
 * Throws an error if accessed outside of a properly configured theme scope.
 *
 * @see ThemeLineHeight
 * @since 0.0.1
 */
internal val LocalThemeLineHeight =
    staticCompositionLocalOf<ThemeLineHeight> {
        error("No implementation")
    }

/**
 * CompositionLocal provider for the current [ThemeSpacing] tokens.
 *
 * Throws an error if accessed outside of a properly configured theme scope.
 *
 * @see ThemeSpacing
 * @since 0.0.1
 */
internal val LocalThemeSpacing =
    staticCompositionLocalOf<ThemeSpacing> {
        error("No implementation")
    }

/**
 * CompositionLocal provider for the current [ThemeTypography] styles.
 *
 * Throws an error if accessed outside of a properly configured theme scope.
 *
 * @see ThemeTypography
 * @since 0.0.1
 */
internal val LocalThemeTypography =
    staticCompositionLocalOf<ThemeTypography> {
        error("No implementation")
    }
