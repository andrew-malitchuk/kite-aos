package presentation.core.ui.source.kit.molecule.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.toggle.Toggle
import presentation.core.ui.source.kit.atom.icon.IcOutline3

/**
 * A list item with a trailing toggle switch.
 *
 * Combines a leading icon, a label, and a [Toggle] switch at the trailing edge. This is the
 * standard component for boolean settings entries.
 *
 * @param modifier Modifier to be applied to the [BaseListItem].
 * @param text The label text displayed in the item (e.g., "Enable notifications").
 * @param textStyle The [TextStyle] for the label text. Defaults to [Theme.typography.body].
 * @param icon The [ImageVector] icon to display on the leading side.
 * @param iconBackgroundColor Background [Color] for the icon container.
 * @param iconForegroundColor Foreground [Color] for the icon.
 * @param isChecked The current checked state of the toggle.
 * @param onCheckedChange Callback invoked when the user toggles the switch, receiving the new state.
 * @see BaseListItem
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun ToggleListItem(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = Theme.typography.body,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconForegroundColor: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    BaseListItem(
        modifier = modifier,
        icon = icon,
        text = text,
        textStyle = textStyle,
        iconBackgroundColor = iconBackgroundColor,
        iconForegroundColor = iconForegroundColor,
        trailingContent = {
            Toggle(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
            )
        },
    )
}

@Preview
@Composable
private fun ToggleListItemPreviewLight() {
    AppTheme {
        var isChecked by remember { mutableStateOf(false) }
        ToggleListItem(
            text = "Setting item",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.surface,
            isChecked = isChecked,
            onCheckedChange = { isChecked = it },
        )
    }
}

@Preview
@Composable
private fun ToggleListItemPreviewDark() {
    AppTheme {
        var isChecked by remember { mutableStateOf(false) }
        ToggleListItem(
            text = "Setting item",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.surface,
            isChecked = isChecked,
            onCheckedChange = { isChecked = it },
        )
    }
}
