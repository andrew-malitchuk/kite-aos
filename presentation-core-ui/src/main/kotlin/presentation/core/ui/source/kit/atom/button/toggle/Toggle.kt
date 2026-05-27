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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filterNotNull
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.tween.cupertinoTween

/**
 * Design system Toggle (switch) component.
 *
 * Toggles switch the state of a single item on or off. The thumb slides horizontally and supports
 * both tap and drag gestures. Color transitions and aspect-ratio changes are animated using
 * Cupertino-style tweens.
 *
 * @param checked Whether or not this Toggle is checked.
 * @param onCheckedChange Callback invoked when the user toggles the switch. Receives the new
 *   checked state. If `null`, the Toggle will not be interactable unless something else handles
 *   its input events and updates its state.
 * @param modifier Modifier to be applied to the [Column].
 * @param thumbContent Optional composable content drawn inside the thumb.
 * @param colors [ToggleColors] that will be used to resolve the colors used for this Toggle in
 *   different states. See [ToggleDefaults.colors].
 * @param enabled Controls the enabled state of this Toggle. When `false`, this component will not
 *   respond to user input and will appear visually disabled and disabled to accessibility services.
 * @param interactionSource The [MutableInteractionSource] representing the stream of [Interaction]s
 *   for this Toggle. You can create and pass in your own `remember`ed instance to observe
 *   [Interaction]s and customize the appearance / behavior of this Toggle in different states.
 *
 * @see ToggleDefaults
 * @see ToggleColors
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thumbContent: @Composable (() -> Unit)? = null,
    colors: ToggleColors =
        ToggleDefaults.colors(
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
            disabledUncheckedIconColor = Theme.color.inkSubtle.copy(alpha = 0.5f),
        ),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Stretch the thumb horizontally when pressed/hovered for tactile feedback (1.25x aspect ratio)
    val animatedAspectRatio by animateFloatAsState(
        targetValue = if (isPressed || isHovered) 1.25f else 1f,
        animationSpec = AspectRationAnimationSpec,
    )
    val animatedBackground by animateColorAsState(
        targetValue = colors.trackColor(enabled, checked).value,
        animationSpec = ColorAnimationSpec,
    )
    // Animate thumb position: 1f = end (checked), -1f = start (unchecked)
    val animatedAlignment by animateFloatAsState(
        targetValue = if (checked) 1f else -1f,
        animationSpec = AlignmentAnimationSpec,
    )

    // Calculate the drag distance required to trigger a toggle (track width minus thumb diameter)
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

    // Emit checked/unchecked when the accumulated drag crosses the positional threshold
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
                    animatedBackground,
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
 * Represents the colors used by a [Toggle] in different states.
 *
 * See [ToggleDefaults.colors] for the default implementation that follows the design system
 * specifications.
 *
 * @param thumbColor The thumb color when the toggle is enabled.
 * @param disabledThumbColor The thumb color when the toggle is disabled.
 * @param checkedTrackColor The track color when the toggle is checked and enabled.
 * @param checkedIconColor The icon color inside the thumb when checked and enabled.
 * @param uncheckedTrackColor The track color when the toggle is unchecked and enabled.
 * @param uncheckedIconColor The icon color inside the thumb when unchecked and enabled.
 * @param disabledCheckedTrackColor The track color when checked and disabled.
 * @param disabledCheckedIconColor The icon color when checked and disabled.
 * @param disabledUncheckedTrackColor The track color when unchecked and disabled.
 * @param disabledUncheckedIconColor The icon color when unchecked and disabled.
 *
 * @see ToggleDefaults.colors
 * @since 0.0.1
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
     * Represents the color used for the Toggle's thumb, depending on [enabled].
     *
     * @param enabled Whether the Toggle is enabled or not.
     * @return A [State] of [Color] for the thumb.
     *
     * @since 0.0.1
     */
    @Composable
    internal fun thumbColor(enabled: Boolean): State<Color> = rememberUpdatedState(
        if (enabled) {
            thumbColor
        } else {
            disabledThumbColor
        },
    )

    /**
     * Represents the color used for the Toggle's track, depending on [enabled] and [checked].
     *
     * @param enabled Whether the Toggle is enabled or not.
     * @param checked Whether the Toggle is checked or not.
     * @return A [State] of [Color] for the track.
     *
     * @since 0.0.1
     */
    @Composable
    internal fun trackColor(enabled: Boolean, checked: Boolean): State<Color> = rememberUpdatedState(
        if (enabled) {
            if (checked) checkedTrackColor else uncheckedTrackColor
        } else {
            if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
        },
    )

    /**
     * Represents the content color passed to the icon if used.
     *
     * @param enabled Whether the Toggle is enabled or not.
     * @param checked Whether the Toggle is checked or not.
     * @return A [State] of [Color] for the icon content.
     *
     * @since 0.0.1
     */
    @Composable
    internal fun iconColor(enabled: Boolean, checked: Boolean): State<Color> = rememberUpdatedState(
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

/**
 * Contains default values and factory methods for the [Toggle] component.
 *
 * @since 0.0.1
 */
@Immutable
public object ToggleDefaults {
    // Elevation applied to the thumb when the toggle is enabled
    internal val EnabledThumbElevation = 4.dp

    /**
     * The default width of the toggle track.
     *
     * @since 0.0.1
     */
    public val Width: Dp = 51.dp

    /**
     * The default height of the toggle track.
     *
     * @since 0.0.1
     */
    public val Height: Dp = 31.dp

    // Pill shape for the track and thumb
    internal val Shape: CornerBasedShape = CircleShape

    /**
     * Creates a [ToggleColors] instance with the given color overrides.
     *
     * @param thumbColor The thumb color when the toggle is enabled.
     * @param disabledThumbColor The thumb color when the toggle is disabled.
     * @param checkedTrackColor The track color when the toggle is checked and enabled.
     * @param checkedIconColor The icon color when the toggle is checked and enabled.
     * @param uncheckedTrackColor The track color when the toggle is unchecked and enabled.
     * @param uncheckedIconColor The icon color when the toggle is unchecked and enabled.
     * @param disabledCheckedTrackColor The track color when checked and disabled.
     * @param disabledCheckedIconColor The icon color when checked and disabled.
     * @param disabledUncheckedTrackColor The track color when unchecked and disabled.
     * @param disabledUncheckedIconColor The icon color when unchecked and disabled.
     * @return A [ToggleColors] instance configured with the provided colors.
     *
     * @since 0.0.1
     */
    @Composable
    @ReadOnlyComposable
    public fun colors(
        thumbColor: Color = Color.White,
        disabledThumbColor: Color = thumbColor,
        checkedTrackColor: Color, // = Theme.color.primary1
        checkedIconColor: Color, // = Theme.colorScheme.opaqueSeparator
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

// Inset between the track edge and the thumb
private val ThumbPadding = 2.dp

// 300ms Cupertino-style tween shared across toggle animations
private val AspectRationAnimationSpec = cupertinoTween<Float>(durationMillis = 300)
private val ColorAnimationSpec = cupertinoTween<Color>(durationMillis = 300)
private val AlignmentAnimationSpec = AspectRationAnimationSpec
