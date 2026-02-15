package presentation.core.ui.source.kit.atom.button.primary.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.ButtonDefault
import presentation.core.ui.source.kit.atom.button.core.ButtonTypographySet
import presentation.core.ui.core.configuration.AnimationConfiguration

public object PrimaryButtonDefault : ButtonDefault {
    @Composable
    override fun buttonColor(): PrimaryButtonColor = PrimaryButtonColor()

    @Composable
    override fun buttonSizeSet(): PrimaryButtonSizeSet = PrimaryButtonSizeSet()

    @Composable
    override fun animation(): ButtonAnimation =
        object : ButtonAnimation {
            override val duration = AnimationConfiguration.Duration.DEFAULT
            override val easing = LinearEasing
        }

     @Composable
     override fun corner(): Dp = Theme.size.sizeXL

    @Composable
    override fun typography(): ButtonTypographySet = PrimaryButtonTypographySet()
}
