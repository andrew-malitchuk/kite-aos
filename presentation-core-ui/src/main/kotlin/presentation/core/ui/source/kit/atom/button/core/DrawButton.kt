package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import presentation.core.ui.R
import presentation.core.ui.core.configuration.AnimationConfiguration
import presentation.core.ui.source.kit.atom.shape.SquircleShape

/**
 * A composable function that represents a button with optional icons, text, and loading animation.
 *
 * This function creates a button with customizable properties for appearance, such as
 * background color, border color, shape, icon size, text style, and spacing.
 * It also supports a loading state that displays a Lottie animation.
 *
 * @param modifier The modifier to be applied to the button.
 * @param text The text to display on the button.
 * @param startIcon The resource ID of the icon to display at the start of the button, or `null` if no icon is needed.
 * @param endIcon The resource ID of the icon to display at the end of the button, or `null` if no icon is needed.
 * @param backgroundColor The background color of the button.
 * @param foregroundColor The foreground color (text and icon color) of the button.
 * @param borderColor The border color of the button.
 * @param corner The corners of the button.
 * @param iconSize The size of the icons within the button.
 * @param loadingSize The size of the loading indicator within the button.
 * @param borderSize The size of the border around the button.
 * @param spacing The spacing within the button.
 * @param minHeight The minimum height of the button.
 * @param paddings The padding values for the content within the button.
 * @param textStyle The style of the text displayed on the button.
 * @param isLoading A boolean indicating whether the button is in a loading state.
 * @param horizontalArrangement The horizontal arrangement of the layout's children.
 *
 * References:
 *
 * - https://proandroiddev.com/compose-a-compose-button-by-composing-composable-functions-9f275772bd23
 * - https://github.com/aoriani/ComposeButton/tree/main
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
        horizontalArrangement = horizontalArrangement
    ) {
        Row(
            modifier = Modifier
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
                modifier = Modifier
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

