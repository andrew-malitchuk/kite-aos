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
import presentation.core.ui.source.kit.atom.container.IconContainer
import presentation.core.ui.source.kit.atom.icon.IcOutline3
import presentation.core.ui.source.kit.molecule.stepper.ValueStepper

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
    enabled: Boolean = true
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
                suffix = suffix
            )
        }
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
            suffix = ""
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
