package presentation.core.ui.source.kit.atom.gradient

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import presentation.core.styling.core.Theme

/**
 * Creates a vertical gradient [Brush] suitable for full-screen backgrounds.
 *
 * The gradient transitions from the canvas color through a subtle blend with the brand variant
 * to a subtle blend with the disabled color, producing a soft ambient background effect.
 *
 * @return a vertical [Brush] gradient using the current theme colors.
 *
 * @since 0.0.1
 */
@Composable
public fun backgroundGradient(): Brush {
    // 10% blend fractions produce a subtle, non-distracting gradient shift
    val colors =
        listOf(
            Theme.color.canvas,
            lerp(Theme.color.canvas, Theme.color.brandVariant, 0.1f),
            lerp(Theme.color.canvas, Theme.color.disabled, 0.1f),
        )

    return Brush.verticalGradient(
        colors = colors,
    )
}
