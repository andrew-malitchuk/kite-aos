package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp overlay / picture-in-picture icon.
 *
 * @since 0.0.1
 */
public val IcOverlay24: ImageVector
    get() {
        if (_IcOverlay24 != null) {
            return _IcOverlay24!!
        }
        _IcOverlay24 =
            ImageVector.Builder(
                name = "IcOverlay24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(400f, 680f)
                    horizontalLineToRelative(360f)
                    verticalLineToRelative(-240f)
                    lineTo(400f, 440f)
                    verticalLineToRelative(240f)
                    close()
                    moveTo(160f, 800f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(80f, 720f)
                    verticalLineToRelative(-480f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(160f, 160f)
                    horizontalLineToRelative(640f)
                    quadToRelative(33f, 0f, 56.5f, 23.5f)
                    reflectiveQuadTo(880f, 240f)
                    verticalLineToRelative(480f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(800f, 800f)
                    lineTo(160f, 800f)
                    close()
                    moveTo(160f, 720f)
                    horizontalLineToRelative(640f)
                    verticalLineToRelative(-480f)
                    lineTo(160f, 240f)
                    verticalLineToRelative(480f)
                    close()
                    moveTo(160f, 720f)
                    verticalLineToRelative(-480f)
                    verticalLineToRelative(480f)
                    close()
                }
            }.build()

        return _IcOverlay24!!
    }

@Suppress("ObjectPropertyName")
private var _IcOverlay24: ImageVector? = null
