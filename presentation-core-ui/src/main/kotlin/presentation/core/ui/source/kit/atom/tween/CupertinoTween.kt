package presentation.core.ui.source.kit.atom.tween

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween

/**
 * Creates a Cupertino-style [TweenSpec] animation matching iOS view transition timing.
 *
 * Default values replicate the easing curve and duration used by iOS system transitions
 * such as UINavigationController push/pop and UIAlertController presentation.
 *
 * @param T the type of value being animated.
 * @param durationMillis the total duration of the animation in milliseconds.
 * @param delayMillis the delay before the animation starts in milliseconds.
 * @param easing the [Easing] curve. Defaults to [CupertinoEasing].
 * @return a [TweenSpec] configured with Cupertino-style timing.
 *
 * @see CupertinoEasing
 *
 * @since 0.0.1
 */
public fun <T> cupertinoTween(
    durationMillis: Int = CupertinoTransitionDuration,
    delayMillis: Int = 0,
    easing: Easing = CupertinoEasing,
): TweenSpec<T> = tween(
    durationMillis = durationMillis,
    easing = easing,
    delayMillis = delayMillis,
)

/**
 * A [CubicBezierEasing] curve that approximates the default iOS animation easing.
 *
 * The control points (0.2833, 0.99, 0.31833, 0.99) produce a quick start with a very smooth
 * deceleration, characteristic of Cupertino-style transitions.
 *
 * @since 0.0.1
 */
// Suppressed: bezier control point values are derived from iOS system animation curves
@Suppress("MagicNumber")
public val CupertinoEasing: CubicBezierEasing = CubicBezierEasing(0.2833f, 0.99f, 0.31833f, 0.99f)
private const val CupertinoTransitionDuration = 400
