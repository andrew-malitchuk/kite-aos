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
 * This molecule provides a consistent layout with a leading icon, a title text or custom content,
 * and a slot for trailing content (like a toggle, stepper, or action button). It serves as the
 * foundation for all specialised list-item variants in the design system.
 *
 * @param modifier Modifier to be applied to the [SquircleCard].
 * @param icon The [ImageVector] icon to display on the left. Optional if [iconContent] is provided.
 * @param text The title text of the item. Optional if [content] is provided.
 * @param textStyle The [TextStyle] for the title text. Defaults to [Theme.typography.body].
 * @param iconBackgroundColor Background [Color] for the icon container.
 * @param iconForegroundColor Foreground [Color] for the icon.
 * @param enabled Whether the item is interactive. When `false`, clicks are ignored.
 * @param onClick Callback invoked when the user taps the item. Pass `null` to disable click handling.
 * @param trailingContent Optional composable slot rendered at the trailing edge of the row.
 * @param content Optional composable slot that replaces the default [text] rendering.
 * @param iconContent Optional composable slot that replaces the default [icon] rendering.
 * @see SquircleCard
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 * @since 0.0.1
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
    iconContent: @Composable (RowScope.() -> Unit)? = null,
) {
    SquircleCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {},
        enabled = enabled,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.sizeS),
        ) {
            if (iconContent != null) {
                iconContent()
            } else if (icon != null) {
                IconContainer(
                    modifier = Modifier.size(Theme.size.size3XL),
                    icon = icon,
                    backgroundColor = iconBackgroundColor,
                    foregroundColor = iconForegroundColor,
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
                    overflow = TextOverflow.Ellipsis,
                )
            }

            trailingContent?.invoke(this)
        }
    }
}
