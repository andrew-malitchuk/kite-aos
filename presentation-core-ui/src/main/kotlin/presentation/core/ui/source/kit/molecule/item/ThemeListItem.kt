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
import presentation.core.ui.source.kit.atom.icon.IcDark24
import presentation.core.ui.source.kit.atom.icon.IcLight24
import presentation.core.ui.source.kit.atom.icon.IcOutline3
import presentation.core.ui.source.kit.atom.icon.IcWallpaper24

/**
 * Represents the available application theme options.
 *
 * @since 0.0.1
 */
public enum class ThemeOption {
    /** Light colour scheme. */
    Light,

    /** Dark colour scheme. */
    Dark,

    /** Dynamic Material You colour scheme derived from the device wallpaper. */
    MaterialU,
}

/**
 * A list item for selecting the application theme.
 *
 * Displays a leading icon, a label, and a trailing row of icon-toggle buttons for each
 * available [ThemeOption]. The [MaterialU][ThemeOption.MaterialU] button is conditionally
 * shown based on device support via [isMaterialUAvailable].
 *
 * @param modifier Modifier to be applied to the [BaseListItem].
 * @param text The label text displayed in the item (e.g., "App Theme").
 * @param textStyle The [TextStyle] for the label text. Defaults to [Theme.typography.body].
 * @param icon The [ImageVector] icon to display on the leading side.
 * @param iconBackgroundColor Background [Color] for the icon container.
 * @param iconForegroundColor Foreground [Color] for the icon.
 * @param selectedTheme The currently active [ThemeOption].
 * @param isMaterialUAvailable Whether the device supports Material You dynamic theming.
 * @param onThemeChange Callback invoked when the user selects a different [ThemeOption].
 * @see BaseListItem
 * @see ThemeOption
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
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
    onThemeChange: (ThemeOption) -> Unit,
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
                        isSelected = selectedTheme == ThemeOption.MaterialU,
                    )
                }
                IconButton(
                    icon = IcLight24,
                    onClick = { onThemeChange(ThemeOption.Light) },
                    sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                    isSelected = selectedTheme == ThemeOption.Light,
                )
                IconButton(
                    icon = IcDark24,
                    onClick = { onThemeChange(ThemeOption.Dark) },
                    sizes = IconButtonDefault.buttonSizeSet().buttonSize40(),
                    isSelected = selectedTheme == ThemeOption.Dark,
                )
            }
        },
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
            onThemeChange = { currentTheme = it },
        )
    }
}
