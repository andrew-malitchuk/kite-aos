package presentation.core.styling.source.attribute.color

import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.ThemeColor

/**
 * Dark theme color palette for the application.
 *
 * Defines all semantic color tokens for the dark appearance. Surface and ink colors
 * are inverted compared to [attributeLightColorPalette], while brand and status
 * colors remain consistent across both themes.
 *
 * @see attributeLightColorPalette
 * @see ThemeColor
 * @since 0.0.1
 */
internal val attributeDarkColorPalette: ThemeColor =
    ThemeColor(
        // region 1. Brand (Same as Light)
        brand = Color(0xFFFF7A1A), // Main Orange - ARGB: fully opaque, R=255, G=122, B=26
        brandVariant = Color(0xFFD7C3F8), // Lilac - ARGB: fully opaque, R=215, G=195, B=248
        // endregion
        // region 2. Surface (Inverted for Dark)
        canvas = Color(0xFF191919), // Main background (Dark) - near-black at ~10% lightness
        surface = Color(0xFF2C2C2C), // Dark cards - slightly elevated from canvas
        surfaceVariant = Color(0xFF3D3D3D), // Dark accent - subtle differentiation layer
        // endregion
        // region 3. Ink (Inverted for Dark)
        inkMain = Color(0xFFFAFAFA), // Primary text (White) - near-white for maximum contrast
        inkSubtle = Color(0xFF8E8E8E), // Secondary text - ~56% lightness for reduced emphasis
        inkOnBrand = Color(0xFF191919), // Dark text on orange - ensures contrast on brand bg
        // endregion
        // region 4. Outline
        outlineLow = Color(0xFF444444), // Subtle dark divider - low contrast separator
        outlineHigh = Color(0xFFFAFAFA), // Contrast border - high visibility on dark surfaces
        // endregion
        // region 5. Status (Same as Light)
        success = Color(0xFF52C470), // Green - R=82, G=196, B=112
        error = Color(0xFFEF6EA5), // Pink/Red - R=239, G=110, B=165
        warning = Color(0xFFF2D147), // Yellow - R=242, G=209, B=71
        // endregion
        // region 6. Interaction
        disabled = Color(0xFF424242), // Dark disabled state - muted grey at ~26% lightness
        scrim = Color(0xCC000000), // Darker overlay - black at 80% opacity (0xCC alpha)
    )
