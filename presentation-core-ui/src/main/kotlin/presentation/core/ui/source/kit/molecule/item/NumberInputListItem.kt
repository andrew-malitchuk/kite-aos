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
import presentation.core.ui.source.kit.atom.icon.IcOutline3
import presentation.core.ui.source.kit.molecule.stepper.ValueStepper

/**
 * A list item with an integrated [ValueStepper] for numeric input.
 *
 * Combines a leading icon, a label, and a trailing [ValueStepper] that lets the user
 * increment or decrement an integer value within a bounded range.
 *
 * @param modifier Modifier to be applied to the [BaseListItem].
 * @param text The label text displayed in the item (e.g., "Font Size").
 * @param textStyle The [TextStyle] for the label text. Defaults to [Theme.typography.body].
 * @param icon The [ImageVector] icon to display on the leading side.
 * @param iconBackgroundColor Background [Color] for the icon container.
 * @param iconForegroundColor Foreground [Color] for the icon.
 * @param value The current integer value displayed in the stepper.
 * @param onValueChange Callback invoked when the user changes the numeric value.
 * @param range The allowed [IntRange] for the value. Defaults to `0..100`.
 * @param step The increment/decrement step size. Defaults to `1`.
 * @param suffix An optional suffix string appended after the numeric value (e.g., "s" for seconds).
 * @param enabled Whether the item is interactive. When `false`, the stepper and item are disabled.
 * @see BaseListItem
 * @see ValueStepper
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun NumberInputListItem(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = Theme.typography.body,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconForegroundColor: Color,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 0..100,
    step: Int = 1,
    suffix: String = "",
    enabled: Boolean = true,
) {
    BaseListItem(
        modifier = modifier,
        icon = icon,
        text = text,
        textStyle = textStyle,
        iconBackgroundColor = iconBackgroundColor,
        iconForegroundColor = iconForegroundColor,
        enabled = enabled,
        trailingContent = {
            ValueStepper(
                value = value,
                onValueChange = onValueChange,
                range = range,
                step = step,
                suffix = suffix,
            )
        },
    )
}

@Preview
@Composable
private fun NumberInputListItemPreview() {
    AppTheme {
        var value by remember { mutableStateOf(24) }

        NumberInputListItem(
            text = "Font Size",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.surface,
            value = value,
            onValueChange = { value = it },
            range = 8..72,
            suffix = "",
        )
    }
}

@Preview
@Composable
private fun NumberInputListItemSuffixPreview() {
    AppTheme {
        var value by remember { mutableStateOf(24) }

        NumberInputListItem(
            text = "Font Size",
            icon = IcOutline3,
            iconBackgroundColor = Theme.color.brand,
            iconForegroundColor = Theme.color.surface,
            value = value,
            suffix = "s",
            onValueChange = { value = it },
            range = 8..72,
        )
    }
}
