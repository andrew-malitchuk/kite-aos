package presentation.core.styling.core

import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import presentation.core.styling.source.attribute.color.attributeDarkColorPalette

/**
 * Provides a [ThemeColor] palette based on the system's dynamic color scheme (Material You).
 *
 * This function extracts colors from the Android system's dynamic color scheme and maps them
 * to the application's semantic color tokens. It supports both light and dark modes.
 *
 * @param isDark Whether the dark mode palette should be generated.
 * @return A [ThemeColor] instance populated with colors from the system's dynamic scheme.
 */
@Composable
internal fun provideDynamicThemeColor(isDark: Boolean): ThemeColor {
    val context = LocalContext.current

    val dynamicScheme = when {
        isDark -> dynamicDarkColorScheme(context)
        else -> dynamicLightColorScheme(context)
    }

    return remember(dynamicScheme) {
        ThemeColor(
            brand = dynamicScheme.primary,
            brandVariant = dynamicScheme.primaryContainer,
            canvas = dynamicScheme.background,
            surface = dynamicScheme.surface,
            surfaceVariant = dynamicScheme.surfaceVariant,
            inkMain = dynamicScheme.onSurface,
            inkSubtle = dynamicScheme.onSurfaceVariant,
            inkOnBrand = dynamicScheme.onPrimary,
            outlineLow = dynamicScheme.outlineVariant,
            outlineHigh = dynamicScheme.outline,
            success = dynamicScheme.primary,
            error = dynamicScheme.error,
            warning = dynamicScheme.tertiary,
            disabled = dynamicScheme.onSurface.copy(alpha = 0.38f),
            scrim = dynamicScheme.scrim
        )
    }
}