package presentation.core.ui.source.kit.atom.button.icon.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import presentation.core.styling.core.Theme
import presentation.core.ui.core.configuration.AnimationConfiguration
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.IconButtonDefault

/**
 * Default configuration object for [IconButton][presentation.core.ui.source.kit.atom.button.icon.IconButton].
 *
 * Provides factory methods for the default color, size set, animation, and corner radius
 * used by icon buttons across the design system.
 *
 * @see presentation.core.ui.source.kit.atom.button.core.IconButtonDefault
 * @since 0.0.1
 */
public object IconButtonDefault : IconButtonDefault {
    /**
     * Returns the default [IconButtonColor] for icon buttons, sourced from the current [Theme].
     *
     * @return An [IconButtonColor] configured with the current theme colors.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonColor(): IconButtonColor = IconButtonColor(
        containerColor = Theme.color.surfaceVariant,
        contentColor = Theme.color.inkMain,
        selectedContainerColor = Theme.color.brand,
        selectedContentColor = Theme.color.surface,
        disabledContentColor = Theme.color.inkSubtle,
        disabledContainerColor = Theme.color.surfaceVariant,
    )

    /**
     * Returns the default [IconButtonSizeSet] for icon buttons.
     *
     * @return A new [IconButtonSizeSet] instance.
     *
     * @since 0.0.1
     */
    @Composable
    override fun buttonSizeSet(): IconButtonSizeSet = IconButtonSizeSet()

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
     * Returns the default corner radius for icon buttons.
     *
     * @return A [Dp] value sourced from [Theme.spacing.sizeM].
     *
     * @since 0.0.1
     */
    @Composable
    override fun corner(): Dp = Theme.spacing.sizeM
}
