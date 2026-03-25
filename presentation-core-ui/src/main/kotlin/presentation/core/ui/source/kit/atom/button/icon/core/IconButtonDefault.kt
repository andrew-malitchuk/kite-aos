package presentation.core.ui.source.kit.atom.button.icon.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import presentation.core.styling.core.Theme
import presentation.core.ui.core.configuration.AnimationConfiguration
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.IconButtonDefault

public object IconButtonDefault : IconButtonDefault {
    @Composable
    override fun buttonColor(): IconButtonColor = IconButtonColor(
        containerColor = Theme.color.surfaceVariant,
        contentColor = Theme.color.inkMain,
        selectedContainerColor = Theme.color.brand,
        selectedContentColor = Theme.color.surface,
        disabledContentColor = Theme.color.inkSubtle,
        disabledContainerColor = Theme.color.surfaceVariant,
    )

    @Composable
    override fun buttonSizeSet(): IconButtonSizeSet = IconButtonSizeSet()

    @Composable
    override fun animation(): ButtonAnimation = object : ButtonAnimation {
        override val duration = AnimationConfiguration.Duration.DEFAULT
        override val easing = LinearEasing
    }

    @Composable
    override fun corner(): Dp = Theme.spacing.sizeM
}
