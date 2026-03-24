package presentation.core.ui.core.ext

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp

/**
 * Creates a new modifier that applies a clickable behavior to the composable without the
 * default ripple effect.
 *
 * @param onClick An optional lambda function that defines the action to be performed when the
 *        user clicks on the composable. Can be null if click behavior is not desired.
 * @return A new `Modifier` instance with the clickable behavior applied (if `onClick` is not
 *         null) or the original modifier unchanged (if `onClick` is null).
 */
public fun Modifier.noRippleClickable(onClick: (() -> Unit)? = null): Modifier = composed {
    if (onClick != null) {
        this.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
        ) {
            onClick.invoke()
        }
    } else {
        this
    }
}

public fun Modifier.noRippleLongClickable(onClick: (() -> Unit)? = null): Modifier = composed {
    if (onClick != null) {
        this.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = { onClick.invoke() },
            )
        }
    } else {
        this
    }
}

/**
 * Extension to automatically fill the maximum available side
 * while maintaining a square aspect ratio.
 */
public fun Modifier.fillMaxSquare(
    maxWidth: Dp,
    maxHeight: Dp,
    aspectRatio: Float = 1f,
    fraction: Float = 1f,
): Modifier {
    return if (maxWidth < maxHeight) {
        this
            .fillMaxWidth(fraction)
            .aspectRatio(aspectRatio)
    } else {
        this
            .fillMaxHeight(fraction)
            .aspectRatio(aspectRatio)
    }
}
