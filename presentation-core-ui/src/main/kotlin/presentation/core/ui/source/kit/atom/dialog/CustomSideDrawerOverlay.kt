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
 * A custom composable that provides a side drawer overlay with a drag-handle indicator.
 *
 * This drawer slides in and out from either the left or right side of the screen and can be used to
 * display additional options or information. It includes an animated vertical drag handle that
 * expands during drag interactions.
 *
 * @param isDrawerOpen whether the drawer is currently open.
 * @param onDismiss callback invoked when the drawer should be dismissed (tap on mask or swipe).
 * @param drawerContent composable content displayed inside the drawer panel.
 * @param content composable content of the main screen behind the drawer.
 * @param modifier Modifier to be applied to the [Box].
 * @param drawerWidth the width of the drawer panel.
 * @param animationDuration duration of the open/close slide animation in milliseconds.
 * @param maskColor the color of the semi-transparent overlay mask.
 * @param showMask whether the mask overlay is shown when the drawer is open.
 * @param drawerSide the side of the screen the drawer slides in from.
 * @param cornerRadius corner radius for the drawer panel edges.
 * @param dragThresholdFraction fraction of the drawer width that must be dragged to trigger dismissal.
 * @param enableSwipe whether swipe-to-dismiss gestures are enabled on the drawer.
 *
 * @see BlurredCustomSideDrawerOverlay
 * @see DrawerSide
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
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
        remember {
            Animatable(
                if (isDrawerOpen) 0f else drawerWidthPx * (if (drawerSide == DrawerSide.LEFT) -1 else 1),
            )
        }

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
                // Doubled offset creates an accelerated slide-in effect
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
                                        DrawerSide.LEFT ->
                                            newOffset.coerceIn(
                                                -drawerWidthPx,
                                                0f,
                                            )

                                        DrawerSide.RIGHT ->
                                            newOffset.coerceIn(
                                                0f,
                                                drawerWidthPx,
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
                    modifier =
                    Modifier
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
 * Enum class representing the side of the screen where a drawer overlay appears.
 *
 * @since 0.0.1
 */
public enum class DrawerSide {
    LEFT,
    RIGHT,
}
