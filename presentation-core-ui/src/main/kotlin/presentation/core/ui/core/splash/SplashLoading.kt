package presentation.core.ui.core.splash

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.icon.IcOutline1
import presentation.core.ui.source.kit.atom.icon.IcOutline2
import presentation.core.ui.source.kit.atom.icon.IcOutline3
import kotlin.math.sqrt

@Composable
public fun SplashLoading(
    isVisible: Boolean = true,
    exitAnimationDuration: Int = 1000,
    onStartExitAnimation: () -> Unit = {},
) {
    var isEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isEntered = true
    }

    val enterAlpha by animateFloatAsState(
        targetValue = if (isEntered) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "enterAlpha",
    )

    var isExitAnimationStarted by remember { mutableStateOf(false) }
    val baseSize = 144.dp

    LaunchedEffect(isVisible) {
        if (!isVisible && !isExitAnimationStarted) {
            isExitAnimationStarted = true
            onStartExitAnimation()
        }
    }

    val configuration = LocalConfiguration.current
    val screenDiagonal =
        remember {
            sqrt(
                (
                    configuration.screenWidthDp * configuration.screenWidthDp +
                        configuration.screenHeightDp * configuration.screenHeightDp
                    ).toFloat(),
            )
        }

    val exitAnimationScale by animateFloatAsState(
        targetValue = if (isExitAnimationStarted) (screenDiagonal / baseSize.value) * 1.5f else 0f,
        animationSpec =
        tween(
            durationMillis = exitAnimationDuration,
            easing = CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f),
        ),
        label = "exitScale",
    )

    var animState by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            animState += 1
            delay(6000)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (!isExitAnimationStarted) {
            Box(
                modifier =
                Modifier
                    .wrapContentSize()
                    .graphicsLayer { alpha = enterAlpha },
                contentAlignment = Alignment.Center,
            ) {
                val icons = listOf(IcOutline1, IcOutline2, IcOutline3)
                val colors =
                    listOf(
                        Theme.color.outlineLow,
                        Theme.color.error,
                        Theme.color.warning,
                    )
                val sizes = listOf(2.7, 3.7, 5.0)
                val alphas = listOf(1f, 0.7f, 0.4f)

                icons.forEachIndexed { index, icon ->
                    val rotation by animateFloatAsState(
                        targetValue = animState * 360f,
                        animationSpec =
                        tween(
                            5000,
                            delayMillis = 800 + (index * 100),
                            easing = FastOutSlowInEasing,
                        ),
                        label = "rotation",
                    )

                    Image(
                        modifier =
                        Modifier
                            .graphicsLayer { rotationZ = rotation }
                            .requiredSize((8 * 16 * sizes[index]).dp)
                            .padding(16.dp)
                            .alpha(alphas[index]),
                        imageVector = icon,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colors[index]),
                    )
                }
            }
        }

        if (isExitAnimationStarted) {
            val circleColor = Theme.color.brandVariant
            Box(
                modifier =
                Modifier
                    .size(baseSize)
                    .graphicsLayer {
                        scaleX = exitAnimationScale
                        scaleY = exitAnimationScale
                    }
                    .drawWithCache {
                        onDrawBehind {
                            drawCircle(color = circleColor)
                        }
                    },
            )
        }
    }
}
