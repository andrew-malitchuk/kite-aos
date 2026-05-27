package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/**
 * A composable function that represents an animated button.
 *
 * This function creates a button with animated color transitions for its background, foreground, and border.
 * It also supports loading state and customizable properties for appearance and animation.
 *
 * @param text The text to display on the button.
 * @param startIcon The resource ID of the icon to display at the start of the button, or `null` if no icon is needed.
 * @param endIcon The resource ID of the icon to display at the end of the button, or `null` if no icon is needed.
 * @param backgroundColor The background color of the button.
 * @param foregroundColor The foreground color (text and icon color) of the button.
 * @param borderColor The border color of the button.
 * @param corner The corner radius applied to the button shape.
 * @param iconSize The size of the icons within the button.
 * @param borderSize The size of the border around the button.
 * @param spacing The spacing between elements within the button.
 * @param minHeight The minimum height of the button.
 * @param paddings The padding values for the content within the button.
 * @param textStyle The style of the text displayed on the button.
 * @param animationDuration The duration of the color animation in milliseconds.
 * @param animationEasing The easing function used for the color animation.
 * @param isLoading A boolean indicating whether the button is in a loading state.
 * @param modifier Modifier to be applied to the [AnimateButton].
 * @param loadingSize The size of the loading indicator within the button.
 * @param horizontalArrangement The horizontal alignment of the button content.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 *
 * @see StateButton
 * @see DrawButton
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun AnimateButton(
    text: String,
    startIcon: Int?,
    endIcon: Int?,
    backgroundColor: Color,
    foregroundColor: Color,
    borderColor: Color,
    corner: Dp,
    iconSize: Dp,
    borderSize: Dp,
    spacing: Dp,
    minHeight: Dp,
    paddings: PaddingValues,
    textStyle: TextStyle,
    animationDuration: Int,
    animationEasing: Easing,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingSize: Dp,
    horizontalArrangement: Alignment = Alignment.Center,
) {
    //region core
    // Shared tween spec for all color transitions so they stay in sync
    val colorAnimationSpec =
        tween<Color>(durationMillis = animationDuration, easing = animationEasing)

    // Animate border, background, and foreground colors independently
    val animationBorderColor by animateColorAsState(
        animationSpec = colorAnimationSpec,
        targetValue = borderColor,
        label = "AnimateButton: animationBorderColor",
    )
    val animationBackgroundColor by animateColorAsState(
        animationSpec = colorAnimationSpec,
        targetValue = backgroundColor,
        label = "AnimateButton: animationBackgroundColor",
    )
    val animationForegroundColor by animateColorAsState(
        animationSpec = colorAnimationSpec,
        targetValue = foregroundColor,
        label = "AnimateButton: animationForegroundColor",
    )

    // Apply content-size animation so the button smoothly resizes (e.g. during loading transitions)
    val localModifier =
        modifier.animateContentSize(
            animationSpec =
            tween(
                durationMillis = animationDuration,
                easing = animationEasing,
            ),
        )
    //endregion core

    DrawButton(
        text = text,
        startIcon = startIcon,
        endIcon = endIcon,
        backgroundColor = animationBackgroundColor,
        foregroundColor = animationForegroundColor,
        borderColor = animationBorderColor,
        corner = corner,
        iconSize = iconSize,
        borderSize = borderSize,
        spacing = spacing,
        minHeight = minHeight,
        paddings = paddings,
        textStyle = textStyle,
        modifier = localModifier,
        isLoading = isLoading,
        loadingSize = loadingSize,
        horizontalArrangement = horizontalArrangement,
    )
}
