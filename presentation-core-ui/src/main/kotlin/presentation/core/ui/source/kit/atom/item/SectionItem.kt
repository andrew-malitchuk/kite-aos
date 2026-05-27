package presentation.core.ui.source.kit.atom.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme

/**
 * A simple item representing a section title.
 *
 * This component is used to display a header or label for a section within a list or a screen.
 * It expands to the full width of its container by default.
 *
 * @param title the text to display as the section title.
 * @param modifier Modifier to be applied to the [Text].
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun SectionItem(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = Theme.typography.title,
        color = Theme.color.inkMain,
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun SectionItemPreview() {
    AppTheme {
        SectionItem(title = "General Settings")
    }
}
