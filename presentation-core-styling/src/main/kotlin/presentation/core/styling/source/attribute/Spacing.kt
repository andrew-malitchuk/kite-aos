package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.dp
import presentation.core.styling.core.ThemeSpacing

/**
 * Default internal spacing configuration.
 * Uses a scale based on an 8dp/4dp grid to ensure structural alignment and consistency.
 */
internal val attributeSpacing: ThemeSpacing =
    ThemeSpacing(
        sizeXXS = 2.dp,
        sizeXS = 4.dp,
        sizeS = 8.dp,
        sizeM = 12.dp,
        sizeL = 16.dp, // The most common padding in Android
        sizeXL = 24.dp,
        size2XL = 32.dp,
        size3XL = 48.dp,
        size4XL = 64.dp,
        size5XL = 80.dp,
    )
