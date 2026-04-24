package presentation.core.styling.source.attribute

import androidx.compose.ui.unit.dp
import presentation.core.styling.core.ThemeSize

/**
 * Default internal sizing configuration for structural UI elements.
 *
 * Provides a T-shirt sizing scale (XXS through 5XL) used for icons, avatars,
 * touch targets, and container dimensions. All values are specified in
 * density-independent pixels (dp).
 *
 * @see ThemeSize
 * @see attributeSpacing
 * @since 0.0.1
 */
internal val attributeSize: ThemeSize =
    ThemeSize(
        sizeXXS = 2.dp, // 2dp - minimal element size (e.g., thin dividers)
        sizeXS = 4.dp, // 4dp - extra-small decorative elements
        sizeS = 8.dp, // 8dp - small icons or indicators
        sizeM = 12.dp, // 12dp - medium elements
        sizeL = 16.dp, // 16dp - standard icon size, most common in Android
        sizeXL = 24.dp, // 24dp - large icons
        size2XL = 32.dp, // 32dp - avatar or badge size
        size3XL = 48.dp, // 48dp - minimum recommended touch target
        size4XL = 64.dp, // 64dp - large avatar or thumbnail
        size5XL = 80.dp, // 80dp - hero-sized elements
    )
