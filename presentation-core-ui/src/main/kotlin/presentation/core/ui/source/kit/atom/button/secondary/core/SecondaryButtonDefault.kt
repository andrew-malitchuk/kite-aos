package presentation.core.ui.source.kit.atom.button.secondary.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import presentation.core.ui.core.configuration.AnimationConfiguration
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.ButtonDefault
import presentation.core.ui.source.kit.atom.button.core.ButtonTypographySet

/**
 * Default configuration object for [SecondaryButton][presentation.core.ui.source.kit.atom.button.secondary.SecondaryButton].
 *
 * Provides factory methods for the default color, size set, animation, corner radius, and typography
 * used by secondary buttons across the design system.
 *
 * @see ButtonDefault
 * @since 0.0.1
 */
public object SecondaryButtonDefault : ButtonDefault {
    /**
     * Returns the default [SecondaryButtonColor] for secondary buttons.
     *
     * @return A new [SecondaryButtonColor] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonColor(): SecondaryButtonColor = SecondaryButtonColor()

    /**
     * Returns the default [SecondaryButtonSizeSet] for secondary buttons.
     *
     * @return A new [SecondaryButtonSizeSet] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonSizeSet(): SecondaryButtonSizeSet = SecondaryButtonSizeSet()

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
     * Returns the default corner radius for secondary buttons.
     *
     * Secondary buttons have no rounded corners (0 dp).
     *
     * @return A [Dp] value of 0.dp.
     *
     * @since 0.0.1
     */
    @Composable
    override fun corner(): Dp = 0.dp

    /**
     * Returns the default [SecondaryButtonTypographySet] for secondary buttons.
     *
     * @return A new [SecondaryButtonTypographySet] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun typography(): ButtonTypographySet = SecondaryButtonTypographySet()
}
