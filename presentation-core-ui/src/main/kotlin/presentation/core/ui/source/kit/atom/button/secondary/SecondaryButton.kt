package presentation.core.ui.source.kit.atom.button.secondary

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.ButtonColor
import presentation.core.ui.source.kit.atom.button.core.ButtonSize
import presentation.core.ui.source.kit.atom.button.core.StateButton
import presentation.core.ui.source.kit.atom.button.secondary.core.SecondaryButtonDefault

/**
 * A composable function that represents a secondary button with customizable properties and optional loading state.
 *
 * This function creates a secondary button with predefined styles and supports customizations for
 * appearance, size, and interactions. It includes optional support for loading indicators and icons.
 *
 * @param modifier Modifier to be applied to the [SecondaryButton].
 * @param text The text to display on the button.
 * @param onClick Callback invoked when the user clicks the button. If `null`, the button is not clickable.
 * @param startIcon The resource ID of the icon to display at the start of the button, or `null` if no icon is needed.
 * @param endIcon The resource ID of the icon to display at the end of the button, or `null` if no icon is needed.
 * @param enabled A boolean indicating whether the button is enabled. Default is `true`.
 * @param isLoading A boolean indicating whether the button is in a loading state. Default is `false`.
 * @param colors The [ButtonColor] instance that provides colors for different button states.
 *               Default is provided by [SecondaryButtonDefault.buttonColor].
 * @param sizes The [ButtonSize] instance that provides size configurations for the button.
 * @param corner The corner radius of the button. Default is provided by [SecondaryButtonDefault.corner].
 * @param textStyle The style of the text displayed on the button. Default is [Theme.typography.action].
 * @param animation The [ButtonAnimation] instance that provides animation configurations for the button.
 *                  Default is provided by [SecondaryButtonDefault.animation].
 * @param interactionSource The [MutableInteractionSource] for the button to track interaction states.
 *                          Default is a new [MutableInteractionSource] created with [remember].
 *
 * @see StateButton
 * @see SecondaryButtonDefault
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
    startIcon: Int? = null,
    endIcon: Int? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    colors: ButtonColor = SecondaryButtonDefault.buttonColor(),
    sizes: ButtonSize,
    corner: Dp = SecondaryButtonDefault.corner(),
    textStyle: TextStyle = Theme.typography.action,
    animation: ButtonAnimation = SecondaryButtonDefault.animation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    StateButton(
        text,
        onClick = onClick ?: {},
        startIcon,
        endIcon,
        colors,
        sizes,
        corner,
        textStyle,
        animation,
        modifier,
        enabled,
        interactionSource,
        isLoading,
    )
}
