package presentation.core.ui.source.kit.molecule.item

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import domain.core.source.model.ApplicationModel
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.SquircleCard
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
public fun ApplicationListItem(
    modifier: Modifier = Modifier,
    applicationModel: ApplicationModel,
    onClick: () -> Unit
) {
    val isChosen = applicationModel.chosen ?: false
    val borderColor by animateColorAsState(
        targetValue = if (isChosen) Theme.color.brand else Color.Transparent,
        label = "ApplicationListItemBorderColor"
    )

    BaseListItem(
        modifier = modifier
            .border(
                width = Theme.size.sizeXXS,
                color = borderColor,
                shape = SquircleShape(Theme.size.sizeXL)
            ),
        onClick = onClick,
        iconContent = {
            Box(
                modifier = Modifier
                    .size(Theme.size.size3XL)
                    .clip(SquircleShape(Theme.size.sizeXL))
                    .background(Theme.color.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val iconPainter = remember(applicationModel.packageName, applicationModel.icon) {
                    val drawable = try {
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
                        modifier = Modifier.size(Theme.size.sizeXL),
                        painter = iconPainter,
                        contentDescription = null
                    )
                } else if (applicationModel.icon != 0) {
                    Image(
                        modifier = Modifier.size(Theme.size.sizeXL),
                        painter = painterResource(id = applicationModel.icon),
                        contentDescription = null
                    )
                }
            }
        },
        text = applicationModel.name
    )
}

@Preview
@Composable
private fun ApplicationListItemSelectedPreview() {
    AppTheme {
        ApplicationListItem(
            applicationModel = ApplicationModel(
                id = 1,
                name = "Example App",
                packageName = "com.example.app",
                icon = presentation.core.ui.R.drawable.ic_launcher_foreground,
                chosen = true
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun ApplicationListItemDefaultPreview() {
    AppTheme {
        ApplicationListItem(
            applicationModel = ApplicationModel(
                id = 1,
                name = "Example App",
                packageName = "com.example.app",
                icon = presentation.core.ui.R.drawable.ic_launcher_foreground,
                chosen = false
            ),
            onClick = {}
        )
    }
}