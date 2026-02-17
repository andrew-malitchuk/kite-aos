package presentation.core.ui.source.kit.atom.dialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
public fun BlurredCustomSideDrawerOverlay(
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerWidth: Dp = 128.dp,
    animationDuration: Int = 300,
    maskColor: Color = Color.Black.copy(alpha = 0.5f),
    showMask: Boolean = true,
    drawerSide: DrawerSide = DrawerSide.RIGHT,
    cornerRadius: Dp = 32.dp,
    dragThresholdFraction: Float = 0.5f,
    enableSwipe: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val drawerWidthPx = remember(density, drawerWidth) { with(density) { drawerWidth.toPx() } }

    val hazeState = remember { HazeState() }

    val offsetX = remember {
        Animatable(if (isDrawerOpen) 0f else drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1))
    }

    LaunchedEffect(isDrawerOpen) {
        val targetOffsetX =
            if (isDrawerOpen) 0f else drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1)
        offsetX.animateTo(
            targetValue = targetOffsetX,
            animationSpec = tween(durationMillis = animationDuration),
        )
    }

    var isExpanded by remember { mutableStateOf(false) }
    val baseHeight = Theme.spacing.size4XL
    val expandedHeight = baseHeight * 2

    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) expandedHeight else baseHeight,
        animationSpec = tween(durationMillis = 200),
        label = "divider-height",
    )

    // Optimization: Use derivedStateOf to prevent excessive recompositions during animation
    val progress by remember(drawerWidthPx) {
        derivedStateOf {
            (1f - (abs(offsetX.value) / drawerWidthPx)).coerceIn(0f, 1f)
        }
    }

    val color = Theme.color.surface

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Source content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            content()
        }

        // 2. Blur Overlay (Mask)
        if (isDrawerOpen || abs(offsetX.value) < drawerWidthPx) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            blurRadius = 6.dp * progress,
                            backgroundColor = Color.Transparent
                        )
                    )
                    // Optimization: Draw the mask color in the Draw phase to avoid recomposition
                    .drawBehind {
                        drawRect(maskColor.copy(alpha = maskColor.alpha * progress))
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onDismiss() })
                    },
            )
        }

        // 3. Drawer content
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(drawerWidth)
                .offset { IntOffset(x = offsetX.value.roundToInt(), y = 0) }
                .align(if (drawerSide == DrawerSide.LEFT) Alignment.CenterStart else Alignment.CenterEnd)
                .padding(Theme.spacing.sizeL)
                .clip(SquircleShape(Theme.size.size2XL))
                .drawBehind {
                    drawRect(color)
                }
                .pointerInput(Unit) {
                    if (enableSwipe) {
                        detectDragGestures(
                            onDragStart = { isExpanded = true },
                            onDragEnd = {
                                isExpanded = false
                                scope.launch {
                                    val shouldClose = when (drawerSide) {
                                        DrawerSide.LEFT -> offsetX.value < -drawerWidthPx * dragThresholdFraction
                                        DrawerSide.RIGHT -> offsetX.value > drawerWidthPx * dragThresholdFraction
                                    }
                                    val finalTarget = if (shouldClose) {
                                        drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1)
                                    } else 0f

                                    offsetX.animateTo(finalTarget, tween(animationDuration))
                                    if (shouldClose) onDismiss()
                                }
                            },
                        ) { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffset = offsetX.value + dragAmount.x
                                val clampedOffset = when (drawerSide) {
                                    DrawerSide.LEFT -> newOffset.coerceIn(-drawerWidthPx, 0f)
                                    DrawerSide.RIGHT -> newOffset.coerceIn(0f, drawerWidthPx)
                                }
                                offsetX.snapTo(clampedOffset)
                            }
                        }
                    }
                },
        ) {
            drawerContent()
        }
    }
}