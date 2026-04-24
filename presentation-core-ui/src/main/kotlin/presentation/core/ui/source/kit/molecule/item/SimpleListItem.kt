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
 * A simple clickable list item with a leading icon and label.
 *
 * This is the most basic list-item variant, containing only a leading icon and a single-line
 * text label. It delegates all layout to [BaseListItem] without any trailing content.
 *
 * @param modifier Modifier to be applied to the [BaseListItem].
 * @param text The label text displayed in the item.
 * @param textStyle The [TextStyle] for the label text. Defaults to [Theme.typography.body].
 * @param icon The [ImageVector] icon to display on the leading side.
 * @param iconBackgroundColor Background [Color] for the icon container.
 * @param iconForegroundColor Foreground [Color] for the icon.
 * @param onClick Callback invoked when the user taps the item.
 * @see BaseListItem
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
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
