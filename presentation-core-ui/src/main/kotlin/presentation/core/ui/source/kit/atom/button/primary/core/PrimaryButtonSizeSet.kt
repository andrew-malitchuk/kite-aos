package presentation.core.ui.source.kit.atom.button.primary.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.core.ButtonSize
import presentation.core.ui.source.kit.atom.button.core.ButtonSizeSet

/**
 * Default [ButtonSizeSet] implementation for primary buttons.
 *
 * Provides [ButtonSize] configurations for the five standard button heights (32, 40, 48, 56, 64 dp).
 * Each size defines icon dimensions, border width, content padding, spacing, minimum height, and
 * loading indicator size sourced from the current [Theme].
 *
 * @see ButtonSizeSet
 * @since 0.0.1
 */
public class PrimaryButtonSizeSet : ButtonSizeSet {
    override fun buttonSize32(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeL // 16.dp
        override val borderSize @Composable get() = Theme.spacing.sizeXXS
        override val contentPadding @Composable get() =
            PaddingValues(
                horizontal = Theme.spacing.sizeXL, // 24.dp
                vertical = Theme.spacing.sizeS, // 8.dp
            )
        override val spacing @Composable get() = Theme.spacing.sizeS // 8.dp
        override val minHeight @Composable get() = Theme.spacing.size2XL // 32.dp
        override val loadingSize @Composable get() = Theme.size.sizeL
    }

    override fun buttonSize40(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeL // 16.dp
        override val borderSize @Composable get() = Theme.spacing.sizeXXS
        override val contentPadding @Composable get() =
            PaddingValues(
                horizontal = Theme.spacing.sizeXL, // 24.dp
                vertical = Theme.spacing.sizeM, // 12.dp
            )
        override val spacing @Composable get() = Theme.spacing.sizeS
        override val minHeight @Composable get() = 40.dp
        override val loadingSize @Composable get() = Theme.size.sizeL
    }

    override fun buttonSize48(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeXL // 24.dp
        override val borderSize @Composable get() = Theme.spacing.sizeXXS
        override val contentPadding @Composable get() =
            PaddingValues(
                horizontal = Theme.spacing.sizeXL, // 24.dp
                vertical = Theme.spacing.sizeM, // 12.dp
            )
        override val spacing @Composable get() = Theme.spacing.sizeS
        override val minHeight @Composable get() = Theme.spacing.size3XL // 48.dp
        override val loadingSize @Composable get() = Theme.size.sizeXL
    }

    override fun buttonSize56(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeXL // 24.dp
        override val borderSize @Composable get() = Theme.spacing.sizeXXS
        override val contentPadding @Composable get() =
            PaddingValues(
                horizontal = Theme.spacing.sizeXL, // 24.dp
                vertical = Theme.spacing.sizeL, // 16.dp
            )
        override val spacing @Composable get() = Theme.spacing.sizeS
        override val minHeight @Composable get() = 56.dp
        override val loadingSize @Composable get() = Theme.size.sizeXL
    }

    override fun buttonSize64(): ButtonSize = object : ButtonSize {
        override val iconSize @Composable get() = Theme.size.sizeXL // 24.dp
        override val borderSize @Composable get() = Theme.spacing.sizeXXS
        override val contentPadding @Composable get() =
            PaddingValues(
                horizontal = Theme.spacing.sizeXL, // 24.dp
                vertical = Theme.spacing.sizeXL, // 24.dp
            )
        override val spacing @Composable get() = Theme.spacing.sizeS
        override val minHeight @Composable get() = Theme.spacing.size4XL // 64.dp
        override val loadingSize @Composable get() = Theme.size.sizeXL
    }
}
