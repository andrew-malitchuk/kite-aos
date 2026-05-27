package presentation.core.styling.source.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import domain.core.source.model.ThemeModel
import presentation.core.styling.core.provideDynamicThemeColor
import presentation.core.styling.source.attribute.AttributeTypography
import presentation.core.styling.source.attribute.attributeFontSize
import presentation.core.styling.source.attribute.attributeLineHeight
import presentation.core.styling.source.attribute.attributeSize
import presentation.core.styling.source.attribute.attributeSpacing
import presentation.core.styling.source.attribute.color.attributeDarkColorPalette
import presentation.core.styling.source.attribute.color.attributeLightColorPalette
import presentation.core.styling.source.provider.LocalThemeColor
import presentation.core.styling.source.provider.LocalThemeFontSize
import presentation.core.styling.source.provider.LocalThemeLineHeight
import presentation.core.styling.source.provider.LocalThemeSize
import presentation.core.styling.source.provider.LocalThemeSpacing
import presentation.core.styling.source.provider.LocalThemeTypography

/**
 * The root styling provider for the application.
 *
 * This composable initializes and provides all theme-related tokens (colors, typography,
 * spacing, sizing, font sizes, and line heights) to the UI tree via CompositionLocals.
 * It should be used at the very top of the application or feature root to ensure
 * consistent styling throughout the composition hierarchy.
 *
 * Supported theme modes:
 * - [ThemeModel.Light] -- uses the statically defined light color palette.
 * - [ThemeModel.Dark] -- uses the statically defined dark color palette.
 * - [ThemeModel.MaterialU] -- uses Android's dynamic color (Material You) palette,
 *   automatically selecting light or dark based on the system setting.
 *
 * @param mode The theme mode to apply. Defaults to [ThemeModel.Light].
 * @param content The UI content that will consume the theme tokens.
 * @see presentation.core.styling.core.Theme
 * @see ThemeModel
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun AppTheme(mode: ThemeModel = ThemeModel.Light, content: @Composable () -> Unit) {
    val isSystemDark = isSystemInDarkTheme()

    // Resolve the color palette based on the selected theme mode.
    val currentColorPalette =
        when (mode) {
            ThemeModel.Light -> attributeLightColorPalette
            ThemeModel.Dark -> attributeDarkColorPalette
            ThemeModel.MaterialU -> provideDynamicThemeColor(isSystemDark)
        }

    // Build typography styles (remembered internally to avoid re-allocation).
    val typography = AttributeTypography()

    // Provide all theme tokens to the composition tree via CompositionLocals.
    CompositionLocalProvider(
        LocalThemeColor provides currentColorPalette,
        LocalThemeFontSize provides attributeFontSize,
        LocalThemeSize provides attributeSize,
        LocalThemeLineHeight provides attributeLineHeight,
        LocalThemeSpacing provides attributeSpacing,
        LocalThemeTypography provides typography,
        content = content,
    )
}
