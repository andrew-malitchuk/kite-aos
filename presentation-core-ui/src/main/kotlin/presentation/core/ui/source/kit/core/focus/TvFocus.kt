package presentation.core.ui.source.kit.core.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.FormFactor
import presentation.core.styling.core.LocalFormFactor
import presentation.core.styling.core.Theme

/**
 * Draws a brand-colored focus ring around a component when it is D-pad focused on an Android TV.
 *
 * D-pad users need to see which element the remote is pointing at; touch components give no such
 * cue on their own (ripple only fires on press). This modifier reads focus from the component's
 * own [interactionSource] and paints a [shape]-matched border in [Theme.color.brand] while focused
 * — but **only** on [FormFactor.TV]. On mobile the ring stays transparent, so touch UI is visually
 * unchanged. Apply it early in the modifier chain (before `clip`/`background`) so the ring is drawn
 * at the outer bounds and matches the component's shape.
 *
 * @param interactionSource The same source passed to the component's `clickable`/`toggleable`, so
 *   focus state is shared rather than duplicated.
 * @param shape The component's outline, so the ring hugs its real silhouette (squircle, pill, …).
 * @since 1.2.0
 */
@Composable
public fun Modifier.tvFocusRing(
    interactionSource: InteractionSource,
    shape: Shape,
): Modifier {
    val isTv = LocalFormFactor.current == FormFactor.TV
    val isFocused by interactionSource.collectIsFocusedAsState()
    val ringColor by animateColorAsState(
        targetValue = if (isTv && isFocused) Theme.color.brand else Color.Transparent,
        label = "TvFocusRingColor",
    )
    return this.border(width = FOCUS_RING_WIDTH, color = ringColor, shape = shape)
}

private val FOCUS_RING_WIDTH = 2.dp
