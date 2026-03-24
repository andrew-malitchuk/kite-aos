package presentation.core.ui.source.kit.molecule.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.icon.IcOutline3

/**
 * A list item for language selection.
 *
 * This component displays a label and a list of language options as text buttons.
 *
 * @param modifier The modifier to be applied to the item.
 * @param text The label text
 * @param textStyle The style for the label text.
 * @param icon The icon to display on the left.
 * @param iconBackgroundColor Background color for the icon container.
 * @param iconForegroundColor Foreground color for the icon.
 * @param onClick Callback when a language is selected.
 */
@Composable
public fun SimpleListItem(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = Theme.typography.body,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconForegroundColor: Color,
    onClick: () -> Unit,
) {
    BaseListItem(
        modifier = modifier,
        icon = icon,
        text = text,
        textStyle = textStyle,
        iconBackgroundColor = iconBackgroundColor,
        iconForegroundColor = iconForegroundColor,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun SimpleListItemPreview() {
    AppTheme {
        SimpleListItem(
            text = "Simple",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.surface,
            onClick = {},
        )
    }
}
