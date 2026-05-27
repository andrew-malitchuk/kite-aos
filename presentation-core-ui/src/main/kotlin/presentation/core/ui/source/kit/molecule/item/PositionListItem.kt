package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import presentation.core.ui.source.kit.atom.button.icon.IconButton
import presentation.core.ui.source.kit.atom.button.icon.core.IconButtonDefault
import presentation.core.ui.source.kit.atom.icon.IcArrowLeft24
import presentation.core.ui.source.kit.atom.icon.IcArrowUp24
import presentation.core.ui.source.kit.atom.icon.IcDock24
import presentation.core.ui.source.kit.atom.icon.IcOutline3

/**
 * Represents the available dock positions for layout placement.
 *
 * @since 0.0.1
 */
public enum class DockPosition {
    /** Dock is positioned at the top of the screen. */
    Up,

    /** Dock is positioned at the left side of the screen. */
    Left,
}

/**
 * A list item for selecting a dock position.
 *
 * Displays a leading icon, a label, and a trailing row of icon-toggle buttons that allow the
 * user to choose between [DockPosition.Up] and [DockPosition.Left]. The currently selected
 * position is highlighted.
 *
 * @param modifier Modifier to be applied to the [BaseListItem].
 * @param text The label text displayed in the item (e.g., "Dock Position").
 * @param textStyle The [TextStyle] for the label text. Defaults to [Theme.typography.body].
 * @param icon The [ImageVector] icon to display on the leading side. Defaults to [IcDock24].
 * @param iconBackgroundColor Background [Color] for the icon container.
 * @param iconForegroundColor Foreground [Color] for the icon.
 * @param selectedSide The currently selected [DockPosition].
 * @param onSideSelected Callback invoked when the user selects a different [DockPosition].
 * @see BaseListItem
 * @see DockPosition
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun PositionListItem(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = Theme.typography.body,
    icon: ImageVector = IcDock24,
    iconBackgroundColor: Color,
    iconForegroundColor: Color,
    selectedSide: DockPosition,
    onSideSelected: (DockPosition) -> Unit,
) {
    BaseListItem(
        modifier = modifier,
        icon = icon,
        text = text,
        textStyle = textStyle,
        iconBackgroundColor = iconBackgroundColor,
        iconForegroundColor = iconForegroundColor,
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeXS)) {
                IconButton(
                    icon = IcArrowUp24,
                    onClick = { onSideSelected(DockPosition.Up) },
                    sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                    isSelected = selectedSide == DockPosition.Up,
                )
                IconButton(
                    icon = IcArrowLeft24,
                    onClick = { onSideSelected(DockPosition.Left) },
                    sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                    isSelected = selectedSide == DockPosition.Left,
                )
            }
        },
    )
}

@Preview
@Composable
private fun PositionListItemPreview() {
    AppTheme {
        var currentTheme by remember { mutableStateOf(DockPosition.Up) }
        PositionListItem(
            text = "App Theme",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.surface,
            selectedSide = currentTheme,
            onSideSelected = { currentTheme = it },
        )
    }
}
