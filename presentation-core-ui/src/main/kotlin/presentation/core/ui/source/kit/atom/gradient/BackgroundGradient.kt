package presentation.core.ui.source.kit.atom.gradient

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import presentation.core.styling.core.Theme

@Composable
public fun backgroundGradient(): Brush {
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
