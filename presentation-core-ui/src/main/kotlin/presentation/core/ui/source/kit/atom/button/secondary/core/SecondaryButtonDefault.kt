package presentation.core.ui.source.kit.atom.button.secondary.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.ButtonDefault
import presentation.core.ui.source.kit.atom.button.core.ButtonTypographySet
import presentation.core.ui.core.configuration.AnimationConfiguration

public object SecondaryButtonDefault : ButtonDefault {
    @Composable
    override fun buttonColor(): SecondaryButtonColor = SecondaryButtonColor()

    @Composable
    override fun buttonSizeSet(): SecondaryButtonSizeSet = SecondaryButtonSizeSet()

    @Composable
    override fun animation(): ButtonAnimation =
        object : ButtonAnimation {
            override val duration = AnimationConfiguration.Duration.DEFAULT
            override val easing = LinearEasing
        }

    @Composable
    override fun corner(): Dp = 0.dp

    @Composable
    override fun typography(): ButtonTypographySet = SecondaryButtonTypographySet()
}
