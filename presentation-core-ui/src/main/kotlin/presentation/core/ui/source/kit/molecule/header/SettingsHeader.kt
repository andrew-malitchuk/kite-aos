package presentation.core.ui.source.kit.molecule.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.button.icon.IconButton
import presentation.core.ui.source.kit.atom.button.icon.core.IconButtonDefault
import presentation.core.ui.source.kit.atom.button.secondary.core.SecondaryButtonDefault
import presentation.core.ui.source.kit.atom.icon.IcArrowLeft24
import presentation.core.ui.source.kit.atom.icon.IcMore24
import presentation.core.ui.source.kit.atom.icon.IcOutline3

/**
 * A header composable for settings screens.
 *
 * This header displays a back button (Primary Icon Button) on the left,
 * a centered title text, and a secondary action button (Secondary Icon Button) on the right.
 *
 * @param title The text to display as the title in the center of the header.
 * @param onAction Callback to handle [SettingsHeaderAction]s.
 * @param modifier The modifier to be applied to the header row.
 */
@Composable
public fun SettingsHeader(
    modifier: Modifier = Modifier,
    title: String,
    onAction: (SettingsHeaderAction) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeS)
    ) {
        // Primary Icon Button (Left) -> Back
        IconButton(
            icon = IcArrowLeft24,
            onClick = { onAction(SettingsHeaderAction.OnBackClick) },
            sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
            colors = IconButtonDefault.buttonColor() // Default is Primary (Brand)
        )

        // Title Text (Centered, Weight 1)
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = Theme.typography.title, // Assuming H3 or similar for header title
            color = Theme.color.inkMain
        )

        // Secondary Icon Button (Right) -> More
        IconButton(
            icon = IcMore24, // Arbitrary icon as requested
            onClick = { onAction(SettingsHeaderAction.OnMoreClick) },
            sizes = IconButtonDefault.buttonSizeSet().buttonSize48(),
            colors = SecondaryButtonDefault.buttonColor() // Use Secondary styles
        )
    }
}

/**
 * Represents the actions that can be triggered from the [SettingsHeader].
 */
public sealed interface SettingsHeaderAction {
    /**
     * Action triggered when the back button is clicked.
     */
    public data object OnBackClick : SettingsHeaderAction

    /**
     * Action triggered when the more/settings button is clicked.
     */
    public data object OnMoreClick : SettingsHeaderAction
}

@Preview(showBackground = true)
@Composable
private fun SettingsHeaderPreview() {
    AppTheme {
        SettingsHeader(
            title = "Settings",
            onAction = {}
        )
    }
}
