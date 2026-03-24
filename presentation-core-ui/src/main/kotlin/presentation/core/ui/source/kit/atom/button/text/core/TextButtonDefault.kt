package presentation.core.ui.source.kit.atom.button.text.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import presentation.core.ui.core.configuration.AnimationConfiguration
import presentation.core.ui.source.kit.atom.button.core.ButtonAnimation
import presentation.core.ui.source.kit.atom.button.core.ButtonDefault
import presentation.core.ui.source.kit.atom.button.core.ButtonTypographySet

public object TextButtonDefault : ButtonDefault {
    @Composable
    override fun buttonColor(): TextButtonColor = TextButtonColor()

    @Composable
    override fun buttonSizeSet(): TextButtonSizeSet = TextButtonSizeSet()

    @Composable
    override fun animation(): ButtonAnimation = object : ButtonAnimation {
        override val duration = AnimationConfiguration.Duration.DEFAULT
        override val easing = LinearEasing
    }

    @Composable
    override fun corner(): Dp = 0.dp

    @Composable
    override fun typography(): ButtonTypographySet = TextButtonTypographySet()
}
