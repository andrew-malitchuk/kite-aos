package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcDock24: ImageVector
    get() {
        if (_IcDock24 != null) {
            return _IcDock24!!
        }
        _IcDock24 =
            ImageVector.Builder(
                name = "IcDock24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(5f, 21f)
                    curveTo(4.45f, 21f, 3.979f, 20.804f, 3.588f, 20.413f)
                    curveTo(3.196f, 20.021f, 3f, 19.55f, 3f, 19f)
                    verticalLineTo(5f)
                    curveTo(3f, 4.45f, 3.196f, 3.979f, 3.588f, 3.588f)
                    curveTo(3.979f, 3.196f, 4.45f, 3f, 5f, 3f)
                    horizontalLineTo(19f)
                    curveTo(19.55f, 3f, 20.021f, 3.196f, 20.413f, 3.588f)
                    curveTo(20.804f, 3.979f, 21f, 4.45f, 21f, 5f)
                    verticalLineTo(19f)
                    curveTo(21f, 19.55f, 20.804f, 20.021f, 20.413f, 20.413f)
                    curveTo(20.021f, 20.804f, 19.55f, 21f, 19f, 21f)
                    horizontalLineTo(5f)
                    close()
                    moveTo(5f, 16f)
                    verticalLineTo(19f)
                    horizontalLineTo(19f)
                    verticalLineTo(16f)
                    horizontalLineTo(5f)
                    close()
                    moveTo(5f, 14f)
                    horizontalLineTo(19f)
                    verticalLineTo(5f)
                    horizontalLineTo(5f)
                    verticalLineTo(14f)
                    close()
                }
            }.build()

        return _IcDock24!!
    }

@Suppress("ObjectPropertyName")
private var _IcDock24: ImageVector? = null
