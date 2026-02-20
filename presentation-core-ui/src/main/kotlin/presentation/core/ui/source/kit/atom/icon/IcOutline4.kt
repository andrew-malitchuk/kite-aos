package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcOutline4: ImageVector
    get() {
        if (_IcOutline4 != null) {
            return _IcOutline4!!
        }
        _IcOutline4 = ImageVector.Builder(
            name = "IcOutline4",
            defaultWidth = 380.dp,
            defaultHeight = 380.dp,
            viewportWidth = 380f,
            viewportHeight = 380f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 4f
            ) {
                moveTo(190f, 28f)
                curveTo(279.47f, 28f, 352f, 100.53f, 352f, 190f)
                curveTo(352f, 279.47f, 279.47f, 352f, 190f, 352f)
                curveTo(100.53f, 352f, 28f, 279.47f, 28f, 190f)
                curveTo(28f, 100.53f, 100.53f, 28f, 190f, 28f)
                close()
            }
        }.build()

        return _IcOutline4!!
    }

@Suppress("ObjectPropertyName")
private var _IcOutline4: ImageVector? = null
