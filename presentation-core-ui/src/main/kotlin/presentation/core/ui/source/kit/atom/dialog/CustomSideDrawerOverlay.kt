package presentation.core.ui.source.kit.atom.dialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import presentation.core.styling.core.Theme
import kotlin.math.roundToInt

/**
 * A custom composable that provides a side drawer overlay.
 * This drawer slides in and out from either the left or right side of the screen and can be used to
 * display additional options or information.
 *
 * @param isDrawerOpen Boolean indicating whether the drawer is open.
 * @param onDismiss Callback function that gets called when the drawer should be dismissed.
 * @param drawerContent Composable content that is displayed inside the drawer.
 * @param content Composable content of the main screen.
 * @param modifier Modifier to be applied to the drawer overlay container.
 * @param drawerWidth Width of the drawer in Dp.
 * @param animationDuration Duration of the drawer open/close animation in milliseconds.
 * @param maskColor Color of the mask overlay when the drawer is open.
 * @param showMask Boolean indicating whether to show the mask overlay.
 * @param drawerSide Side of the screen where the drawer appears (left or right).
 * @param cornerRadius Corner radius of the drawer for rounded edges.
 * @param dragThresholdFraction Fraction of the drawer's width that must be dragged to open/close it.
 * @param enableSwipe Boolean indicating whether swipe gestures are enabled to open/close the drawer.
 */
@Composable
public fun CustomSideDrawerOverlay(
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerWidth: Dp = 300.dp,
    animationDuration: Int = 300,
    maskColor: Color = Color.Black.copy(alpha = 0.5f),
    showMask: Boolean = false,
    drawerSide: DrawerSide = DrawerSide.RIGHT,
    cornerRadius: Dp = 32.dp,
    dragThresholdFraction: Float = 0.5f,
    enableSwipe: Boolean = true,
) {
    // Coroutine scope for managing animations
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current

    // Width of the drawer in pixels
    val drawerWidthPx = with(density) { drawerWidth.toPx() }

    // Offset for the drawer animation
    val offsetX =
        remember { Animatable(if (isDrawerOpen) 0f else drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1)) }

    // Launch animation when the drawer state changes
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

    Box(modifier = modifier.fillMaxSize()) {
        // Main screen content
        content()

        // Mask overlay when the drawer is open
        if (isDrawerOpen) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(maskColor)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { onDismiss() })
                        },
            )
        }

        // Drawer content

        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(drawerWidth)
                    .offset { IntOffset(x = 2 * offsetX.value.roundToInt(), y = 0) }
                    .align(if (drawerSide == DrawerSide.LEFT) Alignment.CenterStart else Alignment.CenterEnd)
                    .pointerInput(Unit) {
                        if (enableSwipe) {
                            detectDragGestures(
                                onDragStart = {
                                    isExpanded = true
                                },
                                onDragEnd = {
                                    isExpanded = false
                                    scope.launch {
                                        val shouldClose =
                                            when (drawerSide) {
                                                DrawerSide.LEFT -> offsetX.value < -drawerWidthPx * dragThresholdFraction
                                                DrawerSide.RIGHT -> offsetX.value > drawerWidthPx * dragThresholdFraction
                                            }

                                        val finalTarget =
                                            if (shouldClose) {
                                                drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1)
                                            } else {
                                                0f
                                            }

                                        offsetX.animateTo(
                                            targetValue = finalTarget,
                                            animationSpec = tween(durationMillis = animationDuration),
                                        )

                                        if (shouldClose) {
                                            onDismiss()
                                        }
                                    }
                                },
                            ) { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    val newOffset = offsetX.value + dragAmount.x

                                    val clampedOffset =
                                        when (drawerSide) {
                                            DrawerSide.LEFT -> newOffset.coerceIn(
                                                -drawerWidthPx,
                                                0f
                                            )

                                            DrawerSide.RIGHT -> newOffset.coerceIn(
                                                0f,
                                                drawerWidthPx
                                            )
                                        }

                                    offsetX.snapTo(clampedOffset)
                                }
                            }
                        }
                    },
//                .then(
//                    if (isDrawerOpen) Modifier.shadow(
//                        elevation = 16.dp,
//                        shape = RoundedCornerShape(cornerRadius)
//                    )
//                    else Modifier
//                )
        ) {
            // Content inside the drawer

            Row(
                modifier =
                    Modifier
                        .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(Theme.spacing.sizeL)
                        .height(animatedHeight)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Theme.color.surface),
                )
                drawerContent()
            }
        }
    }
}

/**
 * Enum class representing the side of the screen where the drawer appears.
 */
public enum class DrawerSide {
    LEFT,
    RIGHT,
}
