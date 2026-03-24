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
 * This component initializes and provides all theme-related tokens (colors, typography, spacing)
 * to the UI tree via CompositionLocals. It should be used at the very top of the application
 * or feature root to ensure consistent styling.
 *
 * @param mode The theme mode to apply (Light, Dark, or MaterialU for dynamic colors).
 * @param content The UI content that will consume the theme.
 */
@Composable
public fun AppTheme(mode: ThemeModel = ThemeModel.Light, content: @Composable () -> Unit) {
    val isSystemDark = isSystemInDarkTheme()
    val currentColorPalette =
        when (mode) {
            ThemeModel.Light -> attributeLightColorPalette
            ThemeModel.Dark -> attributeDarkColorPalette
            ThemeModel.MaterialU -> provideDynamicThemeColor(isSystemDark)
        }

    val typography = AttributeTypography()

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
