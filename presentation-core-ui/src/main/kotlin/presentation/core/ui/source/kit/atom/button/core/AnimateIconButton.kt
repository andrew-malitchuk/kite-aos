package presentation.core.ui.source.kit.atom.button.core

import android.R.attr.text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

/**
 * A composable function that represents an animated button.
 *
 * This function creates a button with animated color transitions for its background, foreground, and border.
 * It also supports loading state and customizable properties for appearance and animation.
 *
 * @param text The text to display on the button.
 * @param icon The resource ID of the icon to display at the start of the button, or `null` if no icon is needed.
 * @param backgroundColor The background color of the button.
 * @param iconColor The icon color of the button.
 * @param borderColor The border color of the button.
 * @param corner The corners of the button.
 * @param iconSize The size of the icons within the button.
 * @param borderSize The size of the border around the button.
 * @param minHeight The minimum height of the button.
 * @param paddings The padding values for the content within the button.
 * @param animationDuration The duration of the color animation in milliseconds.
 * @param animationEasing The easing function used for the color animation.
 * @param isLoading A boolean indicating whether the button is in a loading state.
 * @param modifier The modifier to be applied to the button.
 * @param loadingSize The size of the loading indicator within the button.
 * @param horizontalArrangement The horizontal arrangement of the layout's children.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 *
 * @see StateIconButton
 * @see presentation.core.ui.source.kit.atom.button.core.DrawIconButton
 */
@Composable
internal fun AnimateIconButton(
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    borderColor: Color,
    corner: Dp,
    iconSize: Dp,
    borderSize: Dp,
    minHeight: Dp,
    paddings: PaddingValues,
    animationDuration: Int,
    animationEasing: Easing,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingSize: Dp,
    horizontalArrangement: Alignment = Alignment.Center,
) {
    //region core
    val colorAnimationSpec =
        tween<Color>(durationMillis = animationDuration, easing = animationEasing)

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
        targetValue = iconColor,
        label = "AnimateButton: animationForegroundColor",
    )

    val localModifier =
        modifier.animateContentSize(
            animationSpec =
            tween(
                durationMillis = animationDuration,
                easing = animationEasing,
            ),
        )
    //endregion core

    DrawIconButton(
        icon = icon,
        backgroundColor = animationBackgroundColor,
        iconColor = animationForegroundColor,
        borderColor = animationBorderColor,
        corner = corner,
        iconSize = iconSize,
        borderSize = borderSize,
        minHeight = minHeight,
        paddings = paddings,
        isLoading = isLoading,
        loadingSize = loadingSize,
        horizontalArrangement = horizontalArrangement,
        modifier = localModifier,
    )
}
