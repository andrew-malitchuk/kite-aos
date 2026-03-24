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

public enum class DockPosition {
    Up,
    Left,
}

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
