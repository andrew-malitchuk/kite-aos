package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import domain.core.source.model.ApplicationModel
import presentation.core.styling.core.Theme
import presentation.core.styling.source.attribute.TEN_FOOT_SCALE
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.shape.SquircleShape
import presentation.core.ui.source.kit.core.focus.tvFocusRing

/**
 * A compact application list item that displays only the application icon inside a squircle container.
 *
 * Unlike [ApplicationListItem], this variant omits the application name and package text, rendering
 * only the icon at a fixed 56 dp size. The icon is resolved at runtime from the device's package
 * manager and falls back to a drawable resource when unavailable.
 *
 * @param modifier Modifier to be applied to the [Box].
 * @param applicationModel The [ApplicationModel] containing the application metadata to display.
 * @param onClick Callback invoked when the user taps this item.
 * @see ApplicationListItem
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
 */
@Composable
public fun SimpleApplicationListItem(
    modifier: Modifier = Modifier,
    applicationModel: ApplicationModel,
    onClick: () -> Unit,
) {
    // Container for the application icon. Scales with the 10-foot token multiplier on TV so the
    // shortcut matches the (scaled) control buttons instead of staying a fixed 56dp — otherwise the
    // scaled inner icon overflows the box and the shortcut looks tiny beside the buttons.
    val interactionSource = remember { MutableInteractionSource() }
    val itemShape = SquircleShape(Theme.size.sizeXL)
    val itemSize = if (Theme.is10Foot) 56.dp * TEN_FOOT_SCALE else 56.dp
    Box(
        modifier =
        modifier
            .size(itemSize)
            // TV: draw a D-pad focus ring so the remote user can see the shortcut is focused.
            // Without it the item was reachable but gave no visual cue, so it read as "unreachable".
            .tvFocusRing(interactionSource, itemShape)
            .clip(itemShape)
            .background(Theme.color.surfaceVariant)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val context = LocalContext.current
        // Resolve the application icon from the package manager; fall back to null
        // if the package is not found or the icon cannot be loaded.
        val iconPainter =
            remember(applicationModel.packageName, applicationModel.icon) {
                val drawable =
                    try {
                        context.packageManager.getApplicationIcon(applicationModel.packageName)
                    } catch (e: Exception) {
                        null
                    }

                if (drawable != null) {
                    BitmapPainter(drawable.toBitmap().asImageBitmap())
                } else {
                    null
                }
            }

        if (iconPainter != null) {
            Image(
                modifier = Modifier.size(Theme.size.size3XL),
                painter = iconPainter,
                contentDescription = null,
            )
        } else {
            Icon(
                modifier = Modifier.size(Theme.size.size3XL),
                imageVector = Icons.Outlined.Apps,
                contentDescription = null,
                tint = Theme.color.inkSubtle,
            )
        }
    }
}

@Preview
@Composable
private fun SimpleApplicationListItemSelectedPreview() {
    AppTheme {
        SimpleApplicationListItem(
            applicationModel =
            ApplicationModel(
                id = 1,
                name = "Example App",
                packageName = "com.example.app",
                icon = presentation.core.ui.R.drawable.ic_launcher_foreground,
                chosen = true,
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun SimpleApplicationListItemDefaultPreview() {
    AppTheme {
        SimpleApplicationListItem(
            applicationModel =
            ApplicationModel(
                id = 1,
                name = "Example App",
                packageName = "com.example.app",
                icon = presentation.core.ui.R.drawable.ic_launcher_foreground,
                chosen = false,
            ),
            onClick = {},
        )
    }
}
