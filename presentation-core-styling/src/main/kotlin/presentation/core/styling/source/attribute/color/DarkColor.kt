package presentation.core.styling.source.attribute.color

import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.ThemeColor

internal val attributeDarkColorPalette: ThemeColor =
    ThemeColor(
        // region 1. Brand (Same as Light)
        brand = Color(0xFFFF7A1A), // Main Orange
        brandVariant = Color(0xFFD7C3F8), // Lilac
        // endregion
        // region 2. Surface (Inverted for Dark)
        canvas = Color(0xFF191919), // Main background (Dark)
        surface = Color(0xFF2C2C2C), // Dark cards
        surfaceVariant = Color(0xFF3D3D3D), // Dark accent
        // endregion
        // region 3. Ink (Inverted for Dark)
        inkMain = Color(0xFFFAFAFA), // Primary text (White)
        inkSubtle = Color(0xFF8E8E8E), // Secondary text
        inkOnBrand = Color(0xFF191919), // Dark text on orange
        // endregion
        // region 4. Outline
        outlineLow = Color(0xFF444444), // Subtle dark divider
        outlineHigh = Color(0xFFFAFAFA), // Contrast border
        // endregion
        // region 5. Status (Same as Light)
        success = Color(0xFF52C470), // Green
        error = Color(0xFFEF6EA5), // Pink/Red
        warning = Color(0xFFF2D147), // Yellow
        // endregion
        // region 6. Interaction
        disabled = Color(0xFF424242), // Dark disabled state
        scrim = Color(0xCC000000), // Darker overlay
    )
