package presentation.core.ui.source.kit.atom.button.primary.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.button.core.ButtonTypographySet

/**
 * Default [ButtonTypographySet] implementation for primary buttons.
 *
 * All button sizes use [Theme.typography.action] as the text style.
 *
 * @see ButtonTypographySet
 * @since 0.0.1
 */
public class PrimaryButtonTypographySet : ButtonTypographySet {
    @Composable
    override fun buttonSize32(): TextStyle = Theme.typography.action

    @Composable
    override fun buttonSize40(): TextStyle = Theme.typography.action

    @Composable
    override fun buttonSize48(): TextStyle = Theme.typography.action

    @Composable
    override fun buttonSize56(): TextStyle = Theme.typography.action

    @Composable
    override fun buttonSize64(): TextStyle = Theme.typography.action
}
