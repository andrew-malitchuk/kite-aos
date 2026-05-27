package presentation.core.ui.source.kit.atom.button.primary.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import presentation.core.styling.core.Theme
import presentation.core.ui.core.configuration.AnimationConfiguration
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.ButtonDefault
import presentation.core.ui.source.kit.atom.button.core.ButtonTypographySet

/**
 * Default configuration object for [PrimaryButton][presentation.core.ui.source.kit.atom.button.primary.PrimaryButton].
 *
 * Provides factory methods for the default color, size set, animation, corner radius, and typography
 * used by primary buttons across the design system.
 *
 * @see ButtonDefault
 * @since 0.0.1
 */
public object PrimaryButtonDefault : ButtonDefault {
    /**
     * Returns the default [PrimaryButtonColor] for primary buttons.
     *
     * @return A new [PrimaryButtonColor] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonColor(): PrimaryButtonColor = PrimaryButtonColor()

    /**
     * Returns the default [PrimaryButtonSizeSet] for primary buttons.
     *
     * @return A new [PrimaryButtonSizeSet] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonSizeSet(): PrimaryButtonSizeSet = PrimaryButtonSizeSet()

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
     * Returns the default corner radius for primary buttons.
     *
     * @return A [Dp] value sourced from [Theme.size.sizeXL].
     *
     * @since 0.0.1
     */
    @Composable
    override fun corner(): Dp = Theme.size.sizeXL

    /**
     * Returns the default [PrimaryButtonTypographySet] for primary buttons.
     *
     * @return A new [PrimaryButtonTypographySet] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun typography(): ButtonTypographySet = PrimaryButtonTypographySet()
}
