package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp light mode (sun) icon.
 *
 * @since 0.0.1
 */
public val IcLight24: ImageVector
    get() {
        if (_IcLight24 != null) {
            return _IcLight24!!
        }
        _IcLight24 =
            ImageVector.Builder(
                name = "IcLight24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                group(
                    clipPathData =
                    PathData {
                        moveTo(0f, 0f)
                        horizontalLineToRelative(24f)
                        verticalLineToRelative(24f)
                        horizontalLineToRelative(-24f)
                        close()
                    },
                ) {
                    path(fill = SolidColor(Color(0xFF1F1F1F))) {
                        moveTo(12f, 23.3f)
                        lineTo(8.65f, 20f)
                        horizontalLineTo(4f)
                        verticalLineTo(15.35f)
                        lineTo(0.7f, 12f)
                        lineTo(4f, 8.65f)
                        verticalLineTo(4f)
                        horizontalLineTo(8.65f)
                        lineTo(12f, 0.7f)
                        lineTo(15.35f, 4f)
                        horizontalLineTo(20f)
                        verticalLineTo(8.65f)
                        lineTo(23.3f, 12f)
                        lineTo(20f, 15.35f)
                        verticalLineTo(20f)
                        horizontalLineTo(15.35f)
                        lineTo(12f, 23.3f)
                        close()
                        moveTo(12f, 20.5f)
                        lineTo(14.5f, 18f)
                        horizontalLineTo(18f)
                        verticalLineTo(14.5f)
                        lineTo(20.5f, 12f)
                        lineTo(18f, 9.5f)
                        verticalLineTo(6f)
                        horizontalLineTo(14.5f)
                        lineTo(12f, 3.5f)
                        lineTo(9.5f, 6f)
                        horizontalLineTo(6f)
                        verticalLineTo(9.5f)
                        lineTo(3.5f, 12f)
                        lineTo(6f, 14.5f)
                        verticalLineTo(18f)
                        horizontalLineTo(9.5f)
                        lineTo(12f, 20.5f)
                        close()
                    }
                }
            }.build()

        return _IcLight24!!
    }

@Suppress("ObjectPropertyName")
private var _IcLight24: ImageVector? = null
