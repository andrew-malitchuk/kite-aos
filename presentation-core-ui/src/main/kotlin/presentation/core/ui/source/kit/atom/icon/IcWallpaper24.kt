package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp wallpaper / background image icon.
 *
 * @since 0.0.1
 */
public val IcWallpaper24: ImageVector
    get() {
        if (_IcWallpaper24 != null) {
            return _IcWallpaper24!!
        }
        _IcWallpaper24 =
            ImageVector.Builder(
                name = "IcWallpaper24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(5f, 21f)
                    curveTo(4.45f, 21f, 3.979f, 20.804f, 3.588f, 20.413f)
                    curveTo(3.196f, 20.021f, 3f, 19.55f, 3f, 19f)
                    verticalLineTo(13f)
                    horizontalLineTo(5f)
                    verticalLineTo(19f)
                    horizontalLineTo(11f)
                    verticalLineTo(21f)
                    horizontalLineTo(5f)
                    close()
                    moveTo(13f, 21f)
                    verticalLineTo(19f)
                    horizontalLineTo(19f)
                    verticalLineTo(13f)
                    horizontalLineTo(21f)
                    verticalLineTo(19f)
                    curveTo(21f, 19.55f, 20.804f, 20.021f, 20.413f, 20.413f)
                    curveTo(20.021f, 20.804f, 19.55f, 21f, 19f, 21f)
                    horizontalLineTo(13f)
                    close()
                    moveTo(6f, 17f)
                    lineTo(9f, 13f)
                    lineTo(11.25f, 16f)
                    lineTo(14.25f, 12f)
                    lineTo(18f, 17f)
                    horizontalLineTo(6f)
                    close()
                    moveTo(3f, 11f)
                    verticalLineTo(5f)
                    curveTo(3f, 4.45f, 3.196f, 3.979f, 3.588f, 3.588f)
                    curveTo(3.979f, 3.196f, 4.45f, 3f, 5f, 3f)
                    horizontalLineTo(11f)
                    verticalLineTo(5f)
                    horizontalLineTo(5f)
                    verticalLineTo(11f)
                    horizontalLineTo(3f)
                    close()
                    moveTo(19f, 11f)
                    verticalLineTo(5f)
                    horizontalLineTo(13f)
                    verticalLineTo(3f)
                    horizontalLineTo(19f)
                    curveTo(19.55f, 3f, 20.021f, 3.196f, 20.413f, 3.588f)
                    curveTo(20.804f, 3.979f, 21f, 4.45f, 21f, 5f)
                    verticalLineTo(11f)
                    horizontalLineTo(19f)
                    close()
                    moveTo(15.5f, 10f)
                    curveTo(15.067f, 10f, 14.708f, 9.858f, 14.425f, 9.575f)
                    curveTo(14.142f, 9.292f, 14f, 8.933f, 14f, 8.5f)
                    curveTo(14f, 8.067f, 14.142f, 7.708f, 14.425f, 7.425f)
                    curveTo(14.708f, 7.142f, 15.067f, 7f, 15.5f, 7f)
                    curveTo(15.933f, 7f, 16.292f, 7.142f, 16.575f, 7.425f)
                    curveTo(16.858f, 7.708f, 17f, 8.067f, 17f, 8.5f)
                    curveTo(17f, 8.933f, 16.858f, 9.292f, 16.575f, 9.575f)
                    curveTo(16.292f, 9.858f, 15.933f, 10f, 15.5f, 10f)
                    close()
                }
            }.build()

        return _IcWallpaper24!!
    }

@Suppress("ObjectPropertyName")
private var _IcWallpaper24: ImageVector? = null
