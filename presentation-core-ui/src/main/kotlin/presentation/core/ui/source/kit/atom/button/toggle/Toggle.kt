package presentation.core.ui.source.kit.atom.button.toggle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filterNotNull
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.tween.cupertinoTween

/**
 *  Design Toggle.
 *
 * Togglees toggle the state of a single item on or off.
 *
 * @param checked whether or not this Toggle is checked
 * @param onCheckedChange called when this Toggle is clicked. If `null`, then this Toggle will not
 * be interactable, unless something else handles its input events and updates its state.
 * @param modifier the [Modifier] to be applied to this Toggle
 * @param thumbContent content that will be drawn inside the thumb
 * @param enabled controls the enabled state of this Toggle. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param colors [ToggleColors] that will be used to resolve the colors used for this Toggle in
 * different states. See [ToggleDefaults.colors].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this Toggle. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this Toggle in different states.
 */
@Composable
public fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thumbContent: @Composable (() -> Unit)? = null,
    colors: ToggleColors = ToggleDefaults.colors(
        // The moving thumb part
        thumbColor = Theme.color.outlineHigh,
        disabledThumbColor = Theme.color.outlineHigh,

        // ON state
        checkedTrackColor = Theme.color.brand, // Lilac
        checkedIconColor = Theme.color.inkOnBrand, // White

        // OFF state
        uncheckedTrackColor = Theme.color.brandVariant, // Soft Lilac/Grey
        uncheckedIconColor = Theme.color.inkSubtle, // Blue/Grey

        // Disabled states
        disabledCheckedTrackColor = Theme.color.brand.copy(alpha = 0.33f),
        disabledCheckedIconColor = Theme.color.inkOnBrand.copy(alpha = 0.5f),
        disabledUncheckedTrackColor = Theme.color.outlineLow.copy(alpha = 0.5f),
        disabledUncheckedIconColor = Theme.color.inkSubtle.copy(alpha = 0.5f)
    ),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val animatedAspectRatio by animateFloatAsState(
        targetValue = if (isPressed || isHovered) 1.25f else 1f,
        animationSpec = AspectRationAnimationSpec,
    )
    val animatedBackground by animateColorAsState(
        targetValue = colors.trackColor(enabled, checked).value,
        animationSpec = ColorAnimationSpec,
    )
    val animatedAlignment by animateFloatAsState(
        targetValue = if (checked) 1f else -1f,
        animationSpec = AlignmentAnimationSpec,
    )

    val haptic = LocalHapticFeedback.current

    val positionalThreshold =
        remember {
            (ToggleDefaults.Width - ThumbPadding * 2) -
                    ToggleDefaults.Height
        }

    val density = LocalDensity.current

    val dragThreshold =
        density.run {
            positionalThreshold.toPx()
        }

    var dragDistance by remember {
        mutableFloatStateOf(0f)
    }

    val updatedChecked by rememberUpdatedState(checked)

    LaunchedEffect(dragThreshold) {
        snapshotFlow {
            when {
                dragDistance < 0f -> false
                dragDistance > dragThreshold -> true
                else -> null
            }
        }.filterNotNull().collect(onCheckedChange)
    }

    val thumbColor = colors.thumbColor(enabled).value

    Column(
        modifier
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
            )
            .wrapContentSize(Alignment.Center)
            .requiredSize(ToggleDefaults.Width, ToggleDefaults.Height)
            .clip(ToggleDefaults.Shape)
            .drawBehind {
//                drawRect(animatedBackground)
                drawRect(thumbColor)
            }
            .padding(ThumbPadding),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .aspectRatio(animatedAspectRatio)
                .pointerInput(dragThreshold) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            dragDistance = if (updatedChecked) dragThreshold else 0f
                        },
                        onHorizontalDrag = { c, v ->
                            dragDistance += v
                        },
                    )
                }
                .align(BiasAlignment.Horizontal(animatedAlignment))
                .let {
                    if (enabled) {
                        it.shadow(
                            elevation = ToggleDefaults.EnabledThumbElevation,
                            shape = ToggleDefaults.Shape,
                        )
                    } else {
                        it.clip(ToggleDefaults.Shape)
                    }
                }
                .background(
//                    Color.Cyan
                    animatedBackground
//                    colors.thumbColor(enabled).value
                ),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides colors.iconColor(enabled, checked).value,
            ) {
                thumbContent?.invoke()
            }
        }
    }
}

/**
 * Represents the colors used by a [Toggle] in different states
 *
 * See [ToggleDefaults.colors] for the default implementation that follows Material
 * specifications.
 */
@Immutable
public class ToggleColors internal constructor(
    private val thumbColor: Color,
    private val disabledThumbColor: Color,
    private val checkedTrackColor: Color,
    private val checkedIconColor: Color,
    private val uncheckedTrackColor: Color,
    private val uncheckedIconColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledCheckedIconColor: Color,
    private val disabledUncheckedTrackColor: Color,
    private val disabledUncheckedIconColor: Color,
) {
    /**
     * Represents the color used for the Toggle's thumb, depending on [enabled] and [checked].
     *
     * @param enabled whether the Toggle is enabled or not
     */
    @Composable
    internal fun thumbColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                thumbColor
            } else {
                disabledThumbColor
            },
        )

    /**
     * Represents the color used for the Toggle's track, depending on [enabled] and [checked].
     *
     * @param enabled whether the Toggle is enabled or not
     * @param checked whether the Toggle is checked or not
     */
    @Composable
    internal fun trackColor(
        enabled: Boolean,
        checked: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                if (checked) checkedTrackColor else uncheckedTrackColor
            } else {
                if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
            },
        )

    /**
     * Represents the content color passed to the icon if used
     *
     * @param enabled whether the Toggle is enabled or not
     * @param checked whether the Toggle is checked or not
     */
    @Composable
    internal fun iconColor(
        enabled: Boolean,
        checked: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                if (checked) checkedIconColor else uncheckedIconColor
            } else {
                if (checked) disabledCheckedIconColor else disabledUncheckedIconColor
            },
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ToggleColors) return false

        if (thumbColor != other.thumbColor) return false
        if (checkedTrackColor != other.checkedTrackColor) return false
        if (checkedIconColor != other.checkedIconColor) return false
        if (uncheckedTrackColor != other.uncheckedTrackColor) return false
        if (uncheckedIconColor != other.uncheckedIconColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledCheckedTrackColor != other.disabledCheckedTrackColor) return false
        if (disabledCheckedIconColor != other.disabledCheckedIconColor) return false
        if (disabledUncheckedTrackColor != other.disabledUncheckedTrackColor) return false
        if (disabledUncheckedIconColor != other.disabledUncheckedIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + checkedTrackColor.hashCode()
        result = 31 * result + checkedIconColor.hashCode()
        result = 31 * result + uncheckedTrackColor.hashCode()
        result = 31 * result + uncheckedIconColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledCheckedTrackColor.hashCode()
        result = 31 * result + disabledCheckedIconColor.hashCode()
        result = 31 * result + disabledUncheckedTrackColor.hashCode()
        result = 31 * result + disabledUncheckedIconColor.hashCode()
        return result
    }
}

@Immutable
public object ToggleDefaults {
    internal val EnabledThumbElevation = 4.dp

    public val Width: Dp = 51.dp

    public val Height: Dp = 31.dp

    internal val Shape: CornerBasedShape = CircleShape

    @Composable
    @ReadOnlyComposable
    public fun colors(
        thumbColor: Color = Color.White,
        disabledThumbColor: Color = thumbColor,
        checkedTrackColor: Color, /*= Theme.color.primary1*/
        checkedIconColor: Color, /*= Theme.colorScheme.opaqueSeparator*/
        uncheckedTrackColor: Color,
        uncheckedIconColor: Color = checkedIconColor,
        disabledCheckedTrackColor: Color = checkedTrackColor.copy(alpha = .33f),
        disabledCheckedIconColor: Color = checkedIconColor,
        disabledUncheckedTrackColor: Color = uncheckedTrackColor,
        disabledUncheckedIconColor: Color = checkedIconColor,
    ): ToggleColors =
        ToggleColors(
            thumbColor = thumbColor,
            disabledThumbColor = disabledThumbColor,
            checkedTrackColor = checkedTrackColor,
            checkedIconColor = checkedIconColor,
            uncheckedTrackColor = uncheckedTrackColor,
            uncheckedIconColor = uncheckedIconColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledCheckedIconColor = disabledCheckedIconColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor,
            disabledUncheckedIconColor = disabledUncheckedIconColor,
        )
}

private val ThumbPadding = 2.dp

private val AspectRationAnimationSpec = cupertinoTween<Float>(durationMillis = 300)
private val ColorAnimationSpec = cupertinoTween<Color>(durationMillis = 300)
private val AlignmentAnimationSpec = AspectRationAnimationSpec