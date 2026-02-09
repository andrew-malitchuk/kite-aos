package presentation.core.styling.source.attribute.color

import androidx.compose.ui.graphics.Color
import presentation.core.styling.core.ThemeColor

internal val attributeLightColorPalette: ThemeColor =
    ThemeColor(
        // region 1. Brand (Same for both themes)
        brand = Color(0xFFFF7A1A),        // Main Orange
        brandVariant = Color(0xFFD7C3F8), // Lilac
        // endregion

        // region 2. Surface (Inverted for Light)
        canvas = Color(0xFFFAFAFA),       // Main background (White-ish)
        surface = Color(0xFFFFFFFF),      // Cards and dialogs
        surfaceVariant = Color(0xFFF7F296), // Soft highlight
        // endregion

        // region 3. Ink (Inverted for Light)
        inkMain = Color(0xFF191919),      // Primary text (Black)
        inkSubtle = Color(0xFF757575),    // Secondary text
        inkOnBrand = Color(0xFFFFFFFF),   // White text on orange
        // endregion

        // region 4. Outline
        outlineLow = Color(0xFFB7EDB8),   // Soft divider
        outlineHigh = Color(0xFF191919),  // Strong border
        // endregion

        // region 5. Status (Same for both themes)
        success = Color(0xFF52C470),      // Green
        error = Color(0xFFEF6EA5),        // Pink/Red
        warning = Color(0xFFF2D147),      // Yellow
        // endregion

        // region 6. Interaction
        disabled = Color(0xFFBCDBEB),     // Disabled state
        scrim = Color(0x99191919)         // Overlay
    )