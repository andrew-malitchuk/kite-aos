package presentation.core.ui.source.kit.atom.dialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlin.math.roundToInt

@Composable
public fun CustomBottomDrawerOverlay(
    isDrawerOpen: Boolean,
    onDismiss: () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerHeight: Dp = 400.dp,
    animationDuration: Int = 300,
    maskColor: Color = Color.Black.copy(alpha = 0.5f),
    enableSwipe: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val drawerHeightPx = with(density) { drawerHeight.toPx() }

    val offsetY = remember { Animatable(if (isDrawerOpen) 0f else drawerHeightPx) }

    val animatedWidth by animateDpAsState(
        targetValue = if (isDrawerOpen) 40.dp else 0.dp,
        animationSpec = tween(animationDuration),
        label = "width"
    )

    LaunchedEffect(isDrawerOpen) {
        offsetY.animateTo(
            targetValue = if (isDrawerOpen) 0f else drawerHeightPx,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        content()

        if (isDrawerOpen || offsetY.value < drawerHeightPx) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(maskColor.copy(alpha = maskColor.alpha * (1 - offsetY.value / drawerHeightPx)))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onDismiss() })
                    }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(x = 0, y = offsetY.value.roundToInt()) }
                .fillMaxWidth()
                .height(drawerHeight)
                .pointerInput(Unit) {
                    if (enableSwipe) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    val shouldClose = offsetY.value > drawerHeightPx * 0.5f
                                    val target = if (shouldClose) drawerHeightPx else 0f
                                    offsetY.animateTo(target, tween(animationDuration))
                                    if (shouldClose) onDismiss()
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffset = offsetY.value + dragAmount.y
                                offsetY.snapTo(newOffset.coerceIn(0f, drawerHeightPx))
                            }
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(animatedWidth)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                )

                drawerContent()
            }
        }
    }
}
