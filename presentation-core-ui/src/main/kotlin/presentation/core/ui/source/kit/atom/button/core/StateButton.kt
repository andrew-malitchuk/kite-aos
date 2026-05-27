package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * A composable function that represents a stateful button with customizable properties and animations.
 *
 * This function creates a button that reacts to different interaction states such as hover, press, and focus.
 * It supports customizable colors, sizes, shapes, text styles, and animations.
 * The button also supports a loading state.
 *
 * @param text The text to display on the button.
 * @param onClick Callback invoked when the user clicks the button.
 * @param startIcon The resource ID of the icon to display at the start of the button, or `null` if no icon is needed.
 * @param endIcon The resource ID of the icon to display at the end of the button, or `null` if no icon is needed.
 * @param colors The [ButtonColor] instance that provides colors for different button states.
 * @param sizes The [ButtonSize] instance that provides size configurations for the button.
 * @param corner The corner radius applied to the button shape.
 * @param textStyle The style of the text displayed on the button.
 * @param animation The [ButtonAnimation] instance that provides animation configurations for the button.
 * @param modifier Modifier to be applied to the [StateButton].
 * @param enabled A boolean indicating whether the button is enabled.
 * @param interactionSource The [MutableInteractionSource] for the button to track interaction states.
 * @param isLoading A boolean indicating whether the button is in a loading state.
 * @param horizontalArrangement The horizontal alignment of the button content.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 *
 * @see AnimateButton
 * @see presentation.core.ui.source.kit.atom.button.primary.PrimaryButton
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun StateButton(
    text: String,
    onClick: () -> Unit,
    startIcon: Int?,
    endIcon: Int?,
    colors: ButtonColor,
    sizes: ButtonSize,
    corner: Dp,
    textStyle: TextStyle,
    animation: ButtonAnimation,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    isLoading: Boolean,
    horizontalArrangement: Alignment = Alignment.Center,
) {
    //region core
    // Collect current interaction states from the interaction source
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Build a bitmask of active interaction states for color resolution
    var interactionState = 0
    if (isHovered) interactionState = interactionState.or(ButtonInteractionState.HOVER)
    if (isPressed) interactionState = interactionState.or(ButtonInteractionState.PRESSED)
    if (isFocused) interactionState = interactionState.or(ButtonInteractionState.FOCUSED)

    // Attach clickable behavior; suppress clicks while loading to prevent double-action
    val currentModifier =
        modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = {
                if (!isLoading) {
                    onClick()
                }
            },
            role = Button,
        )

    // Resolve colors based on the current interaction bitmask, enabled, and loading flags
    val backgroundColor = colors.backgroundColor(interactionState, enabled, isLoading).value
    val foregroundColor = colors.foregroundColor(interactionState, enabled, isLoading).value
    val borderColor = colors.borderColor(interactionState, enabled, isLoading).value
    //endregion core

    AnimateButton(
        text = text,
        startIcon = startIcon,
        endIcon = endIcon,
        backgroundColor = backgroundColor,
        foregroundColor = foregroundColor,
        borderColor = borderColor,
        corner = corner,
        iconSize = sizes.iconSize,
        borderSize = sizes.borderSize,
        spacing = sizes.spacing,
        minHeight = sizes.minHeight,
        paddings = sizes.contentPadding,
        textStyle = textStyle,
        animationDuration = animation.duration,
        animationEasing = animation.easing,
        modifier = currentModifier,
        isLoading = isLoading,
        loadingSize = sizes.loadingSize,
        horizontalArrangement = horizontalArrangement,
    )
}
