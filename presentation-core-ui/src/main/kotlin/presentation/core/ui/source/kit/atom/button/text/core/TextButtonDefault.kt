package presentation.core.ui.source.kit.atom.button.text.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import presentation.core.ui.core.configuration.AnimationConfiguration
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.ButtonDefault
import presentation.core.ui.source.kit.atom.button.core.ButtonTypographySet

/**
 * Default configuration object for [TextButton][presentation.core.ui.source.kit.atom.button.text.TextButton].
 *
 * Provides factory methods for the default color, size set, animation, corner radius, and typography
 * used by text buttons across the design system.
 *
 * @see ButtonDefault
 * @since 0.0.1
 */
public object TextButtonDefault : ButtonDefault {
    /**
     * Returns the default [TextButtonColor] for text buttons.
     *
     * @return A new [TextButtonColor] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonColor(): TextButtonColor = TextButtonColor()

    /**
     * Returns the default [TextButtonSizeSet] for text buttons.
     *
     * @return A new [TextButtonSizeSet] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonSizeSet(): TextButtonSizeSet = TextButtonSizeSet()

    /**
     * Returns the default [ButtonAnimation] with linear easing and the standard duration.
     *
     * @return A [ButtonAnimation] configured with [AnimationConfiguration.Duration.DEFAULT] and [LinearEasing].
     *
     * @since 0.0.1
     */
    @Composable
    override fun animation(): ButtonAnimation = object : ButtonAnimation {
        override val duration = AnimationConfiguration.Duration.DEFAULT
        override val easing = LinearEasing
    }

    /**
     * Returns the default corner radius for text buttons.
     *
     * Text buttons have no rounded corners (0 dp).
     *
     * @return A [Dp] value of 0.dp.
     *
     * @since 0.0.1
     */
    @Composable
    override fun corner(): Dp = 0.dp

    /**
     * Returns the default [TextButtonTypographySet] for text buttons.
     *
     * @return A new [TextButtonTypographySet] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun typography(): ButtonTypographySet = TextButtonTypographySet()
}
