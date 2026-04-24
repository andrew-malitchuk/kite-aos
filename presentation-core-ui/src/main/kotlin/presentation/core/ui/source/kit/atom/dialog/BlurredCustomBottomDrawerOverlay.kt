package presentation.core.ui.source.kit.atom.dialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.shape.SquircleShape
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A bottom drawer overlay with a blurred background effect powered by Haze.
 *
 * The drawer slides up from the bottom of the screen with an animated offset. While open,
 * the background content is blurred and overlaid with a semi-transparent mask. The drawer
 * supports swipe-to-dismiss gestures and tap-on-mask dismissal.
 *
 * @param isDrawerOpen whether the drawer is currently open.
 * @param onDismiss callback invoked when the drawer should be dismissed (tap on mask or swipe).
 * @param drawerContent composable content displayed inside the drawer panel.
 * @param content composable content of the main screen behind the drawer.
 * @param modifier Modifier to be applied to the [Box].
 * @param drawerHeight the height of the drawer panel.
 * @param animationDuration duration of the open/close slide animation in milliseconds.
 * @param maskColor the color of the semi-transparent overlay mask.
 * @param enableSwipe whether swipe-to-dismiss gestures are enabled on the drawer.
 *
 * @see CustomBottomDrawerOverlay
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun BlurredCustomBottomDrawerOverlay(
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerHeight: Dp = 128.dp,
    animationDuration: Int = 300,
    maskColor: Color = Color.Black.copy(alpha = 0.3f),
    enableSwipe: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val drawerHeightPx = remember(density, drawerHeight) { with(density) { drawerHeight.toPx() } }

    // Haze state drives the background blur effect; shared between source and effect modifiers
    val hazeState = remember { HazeState() }

    val offsetY =
        remember {
            Animatable(if (isDrawerOpen) 0f else drawerHeightPx)
        }

    LaunchedEffect(isDrawerOpen) {
        val targetOffsetY =
            if (isDrawerOpen) 0f else drawerHeightPx
        offsetY.animateTo(
            targetValue = targetOffsetY,
            animationSpec = tween(durationMillis = animationDuration),
        )
    }

    var isExpanded by remember { mutableStateOf(false) }

    // Optimization: Use derivedStateOf to prevent excessive recompositions during animation
    val progress by remember(drawerHeightPx) {
        derivedStateOf {
            (1f - (abs(offsetY.value) / drawerHeightPx)).coerceIn(0f, 1f)
        }
    }

    val color = Theme.color.surface

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Source content
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
        ) {
            content()
        }

        if (isDrawerOpen || offsetY.value < drawerHeightPx) {
            // Full-screen Blur Overlay
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .hazeEffect(
                        state = hazeState,
                        style =
                        HazeDefaults.style(
                            blurRadius = 6.dp * progress,
                            backgroundColor = Color.Transparent,
                        ),
                    )
                    // Optimization: Draw the mask color in the Draw phase to avoid recomposition
                    .drawBehind {
                        drawRect(maskColor.copy(alpha = maskColor.alpha * progress))
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onDismiss() })
                    },
            )

            // Drawer Panel
            Column(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(drawerHeight)
                    .offset { IntOffset(x = 0, y = offsetY.value.roundToInt()) }
                    .align(Alignment.BottomCenter)
                    .padding(Theme.spacing.sizeL)
                    .clip(SquircleShape(Theme.size.sizeL))
                    .drawBehind {
                        drawRect(color)
                    }
                    .pointerInput(Unit) {
                        if (enableSwipe) {
                            detectDragGestures(
                                onDragStart = { isExpanded = true },
                                onDragEnd = {
                                    scope.launch {
                                        // Close if dragged past 40% of drawer height
                        val shouldClose = offsetY.value > drawerHeightPx * 0.4f
                                        val target = if (shouldClose) drawerHeightPx else 0f
                                        offsetY.animateTo(target, tween(animationDuration))
                                        if (shouldClose) onDismiss()
                                    }
                                },
                            ) { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    val newOffset = offsetY.value + dragAmount.y
                                    offsetY.snapTo(newOffset.coerceIn(0f, drawerHeightPx))
                                }
                            }
                        }
                    },
            ) {
                drawerContent()
            }
        }
    }
}
