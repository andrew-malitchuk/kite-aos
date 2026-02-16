package presentation.core.ui.source.kit.atom.container

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import presentation.core.styling.core.Theme
import presentation.core.styling.source.theme.AppTheme
import presentation.core.ui.source.kit.atom.icon.IcOutline3
import presentation.core.ui.source.kit.atom.shape.SquircleShape

@Composable
public fun IconContainer(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    backgroundColor: Color,
    foregroundColor: Color
) {
    Box(
        modifier = modifier
            .clip(SquircleShape(Theme.size.sizeXL))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(foregroundColor)
        )
    }
}

@Preview
@Composable
private fun IconContainerPreview() {
    AppTheme {
        IconContainer(
            modifier = Modifier.size(64.dp),
            backgroundColor = Theme.color.surface,
            foregroundColor = Theme.color.brand,
            icon = IcOutline3
        )
    }
}