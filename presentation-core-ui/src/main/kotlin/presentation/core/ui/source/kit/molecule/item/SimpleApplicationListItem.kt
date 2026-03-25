package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import domain.core.source.model.ApplicationModel
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.core.ext.noRippleClickable
import presentation.core.ui.source.kit.atom.shape.SquircleShape

/**
 * A list item representing an application.
 *
 * Displays application icon, name and package name.
 * Highlights the item with a brand-colored border if it is chosen.
 *
 * @param modifier The modifier to be applied to the item.
 * @param applicationModel The data model of the application.
 * @param onClick Callback when the item is clicked.
 */
@Composable
public fun SimpleApplicationListItem(
    modifier: Modifier = Modifier,
    applicationModel: ApplicationModel,
    onClick: () -> Unit,
) {
    Box(
        modifier =
        Modifier
            .size(56.dp)
            .clip(SquircleShape(Theme.size.sizeXL))
            .background(Theme.color.surfaceVariant)
            .noRippleClickable(onClick),
        contentAlignment = Alignment.Center,
    ) {
        val context = LocalContext.current
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
        } else if (applicationModel.icon != 0) {
            Image(
                modifier = Modifier.size(Theme.size.size3XL),
                painter = painterResource(id = applicationModel.icon),
                contentDescription = null,
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
