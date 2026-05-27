package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp

/**
 * A composable function that represents a button with optional icons, text, and loading animation.
 *
 * This function creates a button with customizable properties for appearance, such as
 * background color, border color, shape, icon size, text style, and spacing.
 * It also supports a loading state that displays a Lottie animation.
 *
 * @param modifier Modifier to be applied to the [DrawButton].
 * @param text The text to display on the button.
 * @param startIcon The resource ID of the icon to display at the start of the button, or `null` if no icon is needed.
 * @param endIcon The resource ID of the icon to display at the end of the button, or `null` if no icon is needed.
 * @param backgroundColor The background color of the button.
 * @param foregroundColor The foreground color (text and icon color) of the button.
 * @param borderColor The border color of the button.
 * @param corner The corner radius applied to the button shape.
 * @param iconSize The size of the icons within the button.
 * @param loadingSize The size of the loading indicator within the button.
 * @param borderSize The width of the border around the button.
 * @param spacing The spacing between elements within the button.
 * @param minHeight The minimum height of the button.
 * @param paddings The padding values for the content within the button.
 * @param textStyle The style of the text displayed on the button.
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
internal fun DrawButton(
    modifier: Modifier = Modifier,
    text: String,
    startIcon: Int?,
    endIcon: Int?,
    backgroundColor: Color,
    foregroundColor: Color,
    borderColor: Color,
    corner: Dp,
    iconSize: Dp,
    loadingSize: Dp,
    borderSize: Dp,
    spacing: Dp,
    minHeight: Dp,
    paddings: PaddingValues,
    textStyle: TextStyle,
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
        Row(
            modifier =
            Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (startIcon != null) {
                Icon(
                    painter = painterResource(id = startIcon),
                    contentDescription = null,
                    tint = foregroundColor,
                    modifier = Modifier.size(iconSize),
                )
                Spacer(modifier = Modifier.width(spacing))
            }
            Text(
                modifier =
                Modifier
                    .defaultMinSize(minHeight = loadingSize)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                text = text,
                color = foregroundColor,
                style = textStyle,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
            if (endIcon != null) {
                Spacer(modifier = Modifier.width(spacing))
                Icon(
                    painter = painterResource(id = endIcon),
                    contentDescription = null,
                    tint = foregroundColor,
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}
