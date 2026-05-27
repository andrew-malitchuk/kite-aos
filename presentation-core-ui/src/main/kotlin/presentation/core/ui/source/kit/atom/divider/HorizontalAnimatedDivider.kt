package presentation.core.ui.source.kit.atom.divider

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import presentation.core.styling.core.Theme

/**
 * A horizontal divider that fades in and out based on the [isVisible] flag.
 *
 * Uses a [FastOutSlowInEasing] alpha animation over 600ms to smoothly transition the divider
 * visibility.
 *
 * @param modifier Modifier to be applied to the [HorizontalDivider].
 * @param isVisible whether the divider should be visible. Controls the fade animation target.
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun HorizontalAnimatedDivider(modifier: Modifier = Modifier, isVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
    )

    HorizontalDivider(
        modifier =
        modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .alpha(alpha),
        thickness = Theme.size.sizeXXS,
        color = Theme.color.inkSubtle,
    )
}
