package presentation.core.ui.source.kit.molecule.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import presentation.core.styling.core.Theme
import presentation.core.ui.source.kit.atom.SquircleCard
import presentation.core.ui.source.kit.atom.container.IconContainer

/**
 * A base component for list items in settings or menus.
 *
 * This atom provides a consistent layout with a leading icon, a title text or custom content,
 * and a slot for trailing content (like a toggle, stepper, or action button).
 *
 * @param modifier The modifier to be applied to the container.
 * @param icon The icon to display on the left. Optional if [iconContent] is provided.
 * @param text The title text of the item. Optional if [content] is provided.
 * @param textStyle The style for the title text. Defaults to [Theme.typography.body].
 * @param iconBackgroundColor Background color for the icon container.
 * @param iconForegroundColor Foreground color for the icon.
 * @param enabled Whether the item is enabled.
 * @param onClick Optional callback for click events.
 * @param trailingContent Optional composable for content on the right side of the item.
 * @param content Optional composable to override the default [text] implementation.
 * @param iconContent Optional composable to override the default [icon] implementation.
 */
@Composable
public fun BaseListItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: String? = null,
    textStyle: TextStyle = Theme.typography.body,
    iconBackgroundColor: Color = Theme.color.surfaceVariant,
    iconForegroundColor: Color = Theme.color.inkMain,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable (RowScope.() -> Unit)? = null,
    iconContent: @Composable (RowScope.() -> Unit)? = null
) {
    SquircleCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {},
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeS)
        ) {
            if (iconContent != null) {
                iconContent()
            } else if (icon != null) {
                IconContainer(
                    modifier = Modifier.size(Theme.size.size3XL),
                    icon = icon,
                    backgroundColor = iconBackgroundColor,
                    foregroundColor = iconForegroundColor
                )
            }
            
            if (content != null) {
                content()
            } else if (text != null) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = text,
                    color = Theme.color.inkMain,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            trailingContent?.invoke(this)
        }
    }
}
