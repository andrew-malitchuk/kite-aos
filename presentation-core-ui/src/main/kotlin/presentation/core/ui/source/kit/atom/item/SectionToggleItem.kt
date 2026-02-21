package presentation.core.ui.source.kit.atom.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.toggle.Toggle

/**
 * A section item with a toggle switch.
 *
 * This component displays a section title on the left and a toggle switch on the right.
 * It is useful for sections that can be enabled or disabled as a whole.
 *
 * @param title The text to display as the section title.
 * @param checked Whether the toggle is currently checked.
 * @param enabled Whether the toggle is enabled.
 * @param onCheckedChange Callback invoked when the toggle state changes.
 * @param modifier The modifier to be applied to the container.
 */
@Composable
public fun SectionToggleItem(
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Theme.typography.title,
            color = Theme.color.inkMain,
            modifier = Modifier.weight(1f)
        )
        Toggle(
            checked = checked,
            enabled=enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionToggleItemPreview() {
    AppTheme {
        var isChecked by remember { mutableStateOf(true) }
        SectionToggleItem(
            title = "Advanced Settings",
            checked = isChecked,
            enabled = true,
            onCheckedChange = { isChecked = it }
        )
    }
}
