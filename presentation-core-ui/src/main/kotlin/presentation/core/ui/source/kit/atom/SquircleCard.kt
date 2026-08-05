package presentation.core.ui.source.kit.atom

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.shape.SquircleShape
import presentation.core.ui.source.kit.core.focus.tvFocusRing

/**
 * A card composable with a squircle (superellipse) shape that provides press-state color
 * feedback and optional click handling.
 *
 * When [enabled], the card background animates between the surface color and surface variant
 * on press. When disabled, the card renders with a semi-transparent canvas overlay and
 * intercepts all touch events to prevent interaction with child content.
 *
 * @param modifier Modifier to be applied to the [Box].
 * @param onClick callback invoked when the card is clicked. Pass `null` (the default) to leave the
 *        card non-clickable — this matters when the card wraps its own interactive content (e.g. a
 *        text field): an always-clickable card would otherwise swallow focus/taps and the inner
 *        control could never be activated.
 * @param enabled whether the card is interactive. When `false`, clicks are blocked and the
 *        card appearance is dimmed.
 * @param content the composable content displayed inside the card.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun SquircleCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    // On TV the card is reached by D-pad focus rather than touch; treat focus like press so the
    // background lifts and (with the ring below) the focused item is unmistakable from across a room.
    val isFocused by interactionSource.collectIsFocusedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if ((isPressed || isFocused) && enabled) Theme.color.surfaceVariant else Theme.color.surface,
        label = "SquircleCardBackgroundColor",
    )

    val cardShape = SquircleShape(Theme.size.sizeXL)

    Box(
        modifier =
        modifier
            // Ring first (outer bounds) so it hugs the squircle and isn't clipped by the fill.
            .tvFocusRing(interactionSource = interactionSource, shape = cardShape)
            .clip(cardShape)
            .background(
                when (enabled) {
                    true -> backgroundColor
                    false -> Theme.color.canvas.copy(alpha = 0.5f)
                },
            )
            .then(
                // Only intercept clicks/focus when there is an actual handler. A card with no
                // onClick must stay transparent to input so its inner content (text field, etc.)
                // can receive focus and taps.
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                        enabled = enabled,
                    )
                } else {
                    Modifier
                },
            )
            .padding(Theme.spacing.sizeS),
    ) {
        content()

        // Overlay that intercepts all touch events when the card is disabled,
        // preventing child composables from receiving clicks
        if (!enabled) {
            Box(
                modifier =
                Modifier
                    .matchParentSize()
                    .clickable(
                        enabled = true,
                        onClick = {},
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun SquircleCardPreview() {
    AppTheme {
        SquircleCard(modifier = Modifier.padding(16.dp)) {
            Text(text = "This is a squircle card")
        }
    }
}

@Preview
@Composable
private fun SquircleCardDisabledPreview() {
    AppTheme {
        SquircleCard(
            modifier = Modifier.padding(16.dp),
            enabled = false,
        ) {
            Text(text = "This is a squircle card")
        }
    }
}
