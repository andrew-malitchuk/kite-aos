package presentation.core.styling.source.attribute.color

import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.ThemeColor

/**
 * Light theme color palette for the application.
 *
 * Defines all semantic color tokens for the light appearance. Surface and ink colors
 * are inverted compared to [attributeDarkColorPalette], while brand and status
 * colors remain consistent across both themes.
 *
 * @see attributeDarkColorPalette
 * @see ThemeColor
 * @since 0.0.1
 */
internal val attributeLightColorPalette: ThemeColor =
    ThemeColor(
        // region 1. Brand (Same for both themes)
        brand = Color(0xFFFF7A1A), // Main Orange - ARGB: fully opaque, R=255, G=122, B=26
        brandVariant = Color(0xFFD7C3F8), // Lilac - ARGB: fully opaque, R=215, G=195, B=248
        // endregion
        // region 2. Surface (Light variant)
        canvas = Color(0xFFFAFAFA), // Main background (White-ish) - near-white at ~98% lightness
        surface = Color(0xFFFFFFFF), // Cards and dialogs - pure white for elevated containers
        surfaceVariant = Color(0xFFF7F296), // Soft highlight - pale yellow accent for emphasis
        // endregion
        // region 3. Ink (Light variant)
        inkMain = Color(0xFF191919), // Primary text (Black) - near-black for maximum contrast
        inkSubtle = Color(0xFF757575), // Secondary text - ~46% lightness for reduced emphasis
        inkOnBrand = Color(0xFFFFFFFF), // White text on orange - ensures contrast on brand bg
        // endregion
        // region 4. Outline
        outlineLow = Color(0xFFB7EDB8), // Soft divider - light green tint separator
        outlineHigh = Color(0xFF191919), // Strong border - near-black for active/focus states
        // endregion
        // region 5. Status (Same for both themes)
        success = Color(0xFF52C470), // Green - R=82, G=196, B=112
        error = Color(0xFFEF6EA5), // Pink/Red - R=239, G=110, B=165
        warning = Color(0xFFF2D147), // Yellow - R=242, G=209, B=71
        // endregion
        // region 6. Interaction
        disabled = Color(0xFFBCDBEB), // Disabled state - light blue-grey wash
        scrim = Color(0x99191919), // Overlay - near-black at 60% opacity (0x99 alpha)
    )
