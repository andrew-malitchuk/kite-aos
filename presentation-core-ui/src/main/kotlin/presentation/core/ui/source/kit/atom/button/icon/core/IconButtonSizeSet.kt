package presentation.core.ui.source.kit.atom.button.icon.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.core.ButtonSize
import presentation.core.ui.source.kit.atom.button.core.IconButtonSizeSet

public class IconButtonSizeSet : IconButtonSizeSet {
    override fun buttonSize32(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeL // 16.dp
        override val borderSize @Composable get() = 0.dp
        override val contentPadding @Composable get() = PaddingValues(all = Theme.spacing.sizeS) // 8.dp
        override val minHeight @Composable get() = Theme.spacing.size2XL // 32.dp
        override val loadingSize @Composable get() = Theme.size.sizeL
    }

    override fun buttonSize40(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeL // 16.dp
        override val borderSize @Composable get() = 0.dp
        override val contentPadding @Composable get() = PaddingValues(all = Theme.spacing.sizeM) // 12.dp
        override val minHeight @Composable get() = 40.dp
        override val loadingSize @Composable get() = Theme.size.sizeL
    }

    override fun buttonSize48(): ButtonSize = object : ButtonSize {
        // Standard 24dp icon for 48dp button
        override val iconSize @Composable get() = Theme.size.sizeXL
        override val borderSize @Composable get() = 0.dp

        // (48 - 24) / 2 = 12dp padding on each side
        override val contentPadding @Composable get() = PaddingValues(all = Theme.spacing.sizeM)
        override val minHeight @Composable get() = Theme.spacing.size3XL // 48.dp
        override val loadingSize @Composable get() = Theme.size.sizeXL
    }

    override fun buttonSize56(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeXL // 24.dp
        override val borderSize @Composable get() = 0.dp
        override val contentPadding @Composable get() = PaddingValues(all = Theme.spacing.sizeL) // 16.dp
        override val minHeight @Composable get() = 56.dp
        override val loadingSize @Composable get() = Theme.size.sizeXL
    }

    override fun buttonSize64(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeXL // 24.dp
        override val borderSize @Composable get() = 0.dp
        override val contentPadding @Composable get() = PaddingValues(all = Theme.spacing.sizeL) // 16.dp
        override val minHeight @Composable get() = Theme.spacing.size4XL // 64.dp
        override val loadingSize @Composable get() = Theme.size.sizeXL
    }
}
