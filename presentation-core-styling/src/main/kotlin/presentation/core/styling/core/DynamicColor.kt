package presentation.core.styling.core

import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Provides a [ThemeColor] palette based on the system's dynamic color scheme (Material You).
 *
 * This function extracts colors from the Android system's dynamic color scheme and maps them
 * to the application's semantic color tokens. It supports both light and dark modes.
 *
 * The resulting palette is remembered across recompositions as long as the underlying
 * dynamic scheme does not change.
 *
 * @param isDark Whether the dark mode palette should be generated.
 * @return A [ThemeColor] instance populated with colors from the system's dynamic scheme.
 * @see ThemeColor
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun provideDynamicThemeColor(isDark: Boolean): ThemeColor {
    val context = LocalContext.current

    // Select the appropriate Material You color scheme based on the dark mode flag.
    val dynamicScheme = when {
        isDark -> dynamicDarkColorScheme(context)
        else -> dynamicLightColorScheme(context)
    }

    return remember(dynamicScheme) {
        ThemeColor(
            // Map Material You primary to the app's brand accent.
            brand = dynamicScheme.primary,
            // Map Material You primary container to the brand variant (hover/pressed state).
            brandVariant = dynamicScheme.primaryContainer,
            // Background serves as the lowest canvas layer.
            canvas = dynamicScheme.background,
            // Surface for elevated containers like cards and sheets.
            surface = dynamicScheme.surface,
            // Surface variant for subtle differentiation (e.g., input fields).
            surfaceVariant = dynamicScheme.surfaceVariant,
            // High-contrast text/icon color on surfaces.
            inkMain = dynamicScheme.onSurface,
            // Lower-contrast text for secondary information.
            inkSubtle = dynamicScheme.onSurfaceVariant,
            // Contrasting text on top of brand-colored backgrounds.
            inkOnBrand = dynamicScheme.onPrimary,
            // Subtle divider/border using the outline variant.
            outlineLow = dynamicScheme.outlineVariant,
            // Strong border for focus and active states.
            outlineHigh = dynamicScheme.outline,
            // Reuse primary as the success indicator in dynamic themes.
            success = dynamicScheme.primary,
            // Error color from the Material You palette.
            error = dynamicScheme.error,
            // Tertiary color mapped to the warning role.
            warning = dynamicScheme.tertiary,
            // Disabled state uses onSurface with 38% opacity per Material guidelines.
            disabled = dynamicScheme.onSurface.copy(alpha = 0.38f),
            // Scrim overlay for modals and bottom sheets.
            scrim = dynamicScheme.scrim,
        )
    }
}
