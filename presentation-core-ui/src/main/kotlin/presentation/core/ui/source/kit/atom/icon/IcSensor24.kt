package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcSensor24: ImageVector
    get() {
        if (_IcSensor24 != null) {
            return _IcSensor24!!
        }
        _IcSensor24 =
            ImageVector.Builder(
                name = "IcSensor24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(8f, 21f)
                    curveTo(7.45f, 21f, 6.979f, 20.804f, 6.588f, 20.413f)
                    curveTo(6.196f, 20.021f, 6f, 19.55f, 6f, 19f)
                    verticalLineTo(5f)
                    curveTo(6f, 4.45f, 6.196f, 3.979f, 6.588f, 3.588f)
                    curveTo(6.979f, 3.196f, 7.45f, 3f, 8f, 3f)
                    horizontalLineTo(16f)
                    curveTo(16.55f, 3f, 17.021f, 3.196f, 17.413f, 3.588f)
                    curveTo(17.804f, 3.979f, 18f, 4.45f, 18f, 5f)
                    verticalLineTo(19f)
                    curveTo(18f, 19.55f, 17.804f, 20.021f, 17.413f, 20.413f)
                    curveTo(17.021f, 20.804f, 16.55f, 21f, 16f, 21f)
                    horizontalLineTo(8f)
                    close()
                    moveTo(16f, 19f)
                    verticalLineTo(5f)
                    horizontalLineTo(8f)
                    verticalLineTo(19f)
                    horizontalLineTo(16f)
                    close()
                    moveTo(12f, 8f)
                    curveTo(12.283f, 8f, 12.521f, 7.904f, 12.712f, 7.713f)
                    curveTo(12.904f, 7.521f, 13f, 7.283f, 13f, 7f)
                    curveTo(13f, 6.717f, 12.904f, 6.479f, 12.712f, 6.287f)
                    curveTo(12.521f, 6.096f, 12.283f, 6f, 12f, 6f)
                    curveTo(11.717f, 6f, 11.479f, 6.096f, 11.288f, 6.287f)
                    curveTo(11.096f, 6.479f, 11f, 6.717f, 11f, 7f)
                    curveTo(11f, 7.283f, 11.096f, 7.521f, 11.288f, 7.713f)
                    curveTo(11.479f, 7.904f, 11.717f, 8f, 12f, 8f)
                    close()
                    moveTo(0f, 17f)
                    verticalLineTo(10f)
                    horizontalLineTo(2f)
                    verticalLineTo(17f)
                    horizontalLineTo(0f)
                    close()
                    moveTo(3f, 14f)
                    verticalLineTo(7f)
                    horizontalLineTo(5f)
                    verticalLineTo(14f)
                    horizontalLineTo(3f)
                    close()
                    moveTo(22f, 14f)
                    verticalLineTo(7f)
                    horizontalLineTo(24f)
                    verticalLineTo(14f)
                    horizontalLineTo(22f)
                    close()
                    moveTo(19f, 17f)
                    verticalLineTo(10f)
                    horizontalLineTo(21f)
                    verticalLineTo(17f)
                    horizontalLineTo(19f)
                    close()
                }
            }.build()

        return _IcSensor24!!
    }

@Suppress("ObjectPropertyName")
private var _IcSensor24: ImageVector? = null
