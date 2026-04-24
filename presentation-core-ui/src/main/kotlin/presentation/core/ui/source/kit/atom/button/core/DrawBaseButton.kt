package presentation.core.ui.source.kit.atom.button.core

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
 * An internal base component for drawing buttons with consistent decoration and loading behavior.
 *
 * This composable renders a [Box] decorated with a squircle border, background, and content padding.
 * When [isLoading] is `true`, the [content] is replaced by a Lottie loading animation via an
 * [AnimatedContent] transition.
 *
 * @param modifier Modifier to be applied to the [Box].
 * @param backgroundColor The background color of the button container.
 * @param borderColor The border color of the button container.
 * @param corner The corner radius applied to the [SquircleShape].
 * @param borderSize The width of the border stroke.
 * @param paddings The padding values applied inside the button container.
 * @param isLoading Whether the button displays a loading indicator instead of [content].
 * @param loadingSize The size of the Lottie loading animation.
 * @param horizontalArrangement The alignment used to position [content] within the container.
 * @param content The composable content to display when the button is not loading.
 *
 * @see DrawButton
 * @see DrawIconButton
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
internal fun DrawBaseButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    borderColor: Color,
    corner: Dp,
    borderSize: Dp,
    paddings: PaddingValues,
    isLoading: Boolean,
    loadingSize: Dp,
    horizontalArrangement: Alignment = Alignment.Center,
    content: @Composable () -> Unit,
) {
    // Pre-load the Lottie composition so it is ready when isLoading flips to true
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_loading))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )

    // Container with squircle shape, border, background, and animated content sizing
    Box(
        modifier =
        modifier
            .animateContentSize(
                animationSpec =
                tween(
                    durationMillis = AnimationConfiguration.Duration.DEFAULT,
                    easing = LinearOutSlowInEasing,
                ),
            )
            .border(
                width = borderSize,
                color = borderColor,
                shape = SquircleShape(corner),
            )
            .background(
                color = backgroundColor,
                shape = SquircleShape(corner),
            )
            .padding(paddings),
        contentAlignment = horizontalArrangement,
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = AnimationConfiguration.Transition.default(),
            label = "DrawBaseButton: AnimatedContent",
        ) { loading ->
            if (loading) {
                Box(
                    modifier =
                    Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    LottieAnimation(
                        modifier = Modifier.size(loadingSize),
                        composition = composition,
                        progress = { progress },
                    )
                }
            } else {
                content()
            }
        }
    }
}
