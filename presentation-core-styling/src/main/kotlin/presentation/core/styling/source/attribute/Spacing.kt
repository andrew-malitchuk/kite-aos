package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.dp
import presentation.core.styling.core.ThemeSpacing

/**
 * Default internal spacing configuration.
 *
 * Uses a scale based on an 8dp/4dp grid to ensure structural alignment and consistency.
 * These values govern margins, paddings, and gaps between layout elements.
 *
 * @see ThemeSpacing
 * @see attributeSize
 * @since 0.0.1
 */
internal val attributeSpacing: ThemeSpacing =
    ThemeSpacing(
        sizeXXS = 2.dp, // 2dp - hairline spacing (e.g., between icon and badge)
        sizeXS = 4.dp, // 4dp - tight spacing for compact layouts
        sizeS = 8.dp, // 8dp - base grid unit, inner padding for small components
        sizeM = 12.dp, // 12dp - medium padding for card content
        sizeL = 16.dp, // 16dp - most common padding in Android (outer container)
        sizeXL = 24.dp, // 24dp - section separation
        size2XL = 32.dp, // 32dp - large section gaps
        size3XL = 48.dp, // 48dp - prominent visual separation
        size4XL = 64.dp, // 64dp - major layout divisions
        size5XL = 80.dp, // 80dp - maximum spacing for hero sections
    )
