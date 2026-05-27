package presentation.core.ui.source.kit.atom.button.text

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
import presentation.core.ui.source.kit.atom.button.text.core.TextButtonDefault

/**
 * A composable function that represents a customizable text button with optional loading state and interactive behavior.
 *
 * This function creates a text button that allows for customizations such as size, appearance, and interaction handling.
 * It includes optional support for a loading state, custom colors, and animations. The button displays text and can
 * be enabled or disabled, with a callback invoked when clicked.
 *
 * @param modifier Modifier to be applied to the [TextButton].
 * @param text The [String] to be displayed inside the button.
 * @param onClick Callback invoked when the user clicks the button. If `null`, the button is not clickable.
 * @param enabled A boolean indicating whether the button is enabled. Default is `true`, meaning the button is enabled.
 * @param isLoading A boolean indicating whether the button is in a loading state. Default is `false`, meaning no loading state.
 * @param colors The [ButtonColor] instance that provides colors for the button in different states (enabled, disabled, etc.).
 *               Default is provided by [TextButtonDefault.buttonColor].
 * @param sizes The [ButtonSize] instance that defines the size configuration for the button.
 * @param corner The corner radius of the button. Default is provided by [TextButtonDefault.corner].
 * @param textStyle The style of the text displayed inside the button. Default is [Theme.typography.action].
 * @param animation The [ButtonAnimation] instance that defines animation configurations for the button.
 *                  Default is provided by [TextButtonDefault.animation].
 * @param interactionSource The [MutableInteractionSource] for tracking the button's interaction states.
 *                          Default is a new [MutableInteractionSource] created with [remember].
 *
 * @see StateButton
 * @see TextButtonDefault
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun TextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    colors: ButtonColor = TextButtonDefault.buttonColor(),
    sizes: ButtonSize,
    corner: Dp = TextButtonDefault.corner(),
    textStyle: TextStyle = Theme.typography.action,
    animation: ButtonAnimation = TextButtonDefault.animation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    StateButton(
        text,
        onClick = onClick ?: {},
        null,
        null,
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
