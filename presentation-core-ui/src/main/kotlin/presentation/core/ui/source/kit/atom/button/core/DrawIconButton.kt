package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

/**
 * A composable function that represents a icon button with optional loading animation.
 *
 * This function creates a button with customizable properties for appearance, such as background
 * color, border color, shape and icon size.
 * It also supports a loading state that displays a Lottie animation.
 *
 * @param modifier Modifier to be applied to the [DrawIconButton].
 * @param icon The [ImageVector] to display inside the button.
 * @param backgroundColor The background color of the button.
 * @param iconColor The tint color applied to the icon.
 * @param borderColor The border color of the button.
 * @param corner The corner radius applied to the button shape.
 * @param iconSize The size of the icon within the button.
 * @param loadingSize The size of the loading indicator within the button.
 * @param borderSize The width of the border around the button.
 * @param minHeight The minimum height of the button.
 * @param paddings The padding values for the content within the button.
 * @param isLoading A boolean indicating whether the button is in a loading state.
 * @param horizontalArrangement The horizontal alignment of the button content.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
 *
 * @see DrawBaseButton
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun DrawIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    borderColor: Color,
    corner: Dp,
    iconSize: Dp,
    loadingSize: Dp,
    borderSize: Dp,
    minHeight: Dp,
    paddings: PaddingValues,
    isLoading: Boolean,
    horizontalArrangement: Alignment = Alignment.Center,
) {
    DrawBaseButton(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        corner = corner,
        borderSize = borderSize,
        paddings = paddings,
        isLoading = isLoading,
        loadingSize = loadingSize,
        horizontalArrangement = horizontalArrangement,
    ) {
        Box(
            modifier =
            Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}
