package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.SquircleCard
import presentation.core.ui.source.kit.atom.button.icon.IconButton
import presentation.core.ui.source.kit.atom.button.icon.core.IconButtonDefault
import presentation.core.ui.source.kit.atom.container.IconContainer
import presentation.core.ui.source.kit.atom.icon.IcDark24
import presentation.core.ui.source.kit.atom.icon.IcLight24
import presentation.core.ui.source.kit.atom.icon.IcWallpaper24
import presentation.core.ui.source.kit.atom.icon.IcOutline3

public enum class ThemeOption {
    Light, Dark, MaterialU
}

@Composable
public fun ThemeListItem(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = Theme.typography.body,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconForegroundColor: Color,
    selectedTheme: ThemeOption,
    isMaterialUAvailable: Boolean,
    onThemeChange: (ThemeOption) -> Unit
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
                if (isMaterialUAvailable) {
                    IconButton(
                        icon = IcWallpaper24,
                        onClick = { onThemeChange(ThemeOption.MaterialU) },
                        sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                        isSelected = selectedTheme == ThemeOption.MaterialU
                    )
                }
                IconButton(
                    icon = IcLight24,
                    onClick = { onThemeChange(ThemeOption.Light) },
                    sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                    isSelected = selectedTheme == ThemeOption.Light
                )
                IconButton(
                    icon = IcDark24,
                    onClick = { onThemeChange(ThemeOption.Dark) },
                    sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                    isSelected = selectedTheme == ThemeOption.Dark
                )
            }
        }
    )
}

@Preview
@Composable
private fun ThemeListItemPreview() {
    AppTheme {
        var currentTheme by remember { mutableStateOf(ThemeOption.MaterialU) }
        ThemeListItem(
            text = "App Theme",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.surface,
            selectedTheme = currentTheme,
            isMaterialUAvailable = true,
            onThemeChange = { currentTheme = it }
        )
    }
}
