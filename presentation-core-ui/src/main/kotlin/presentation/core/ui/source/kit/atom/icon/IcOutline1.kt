package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcOutline1: ImageVector
    get() {
        if (_IcOutline1 != null) {
            return _IcOutline1!!
        }
        _IcOutline1 =
            ImageVector.Builder(
                name = "IcOutline1",
                defaultWidth = 380.dp,
                defaultHeight = 380.dp,
                viewportWidth = 380f,
                viewportHeight = 380f,
            ).apply {
                path(
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 4f,
                ) {
                    moveTo(135.58f, 56f)
                    curveTo(165.89f, 26.67f, 214.11f, 26.67f, 244.42f, 56f)
                    curveTo(254.18f, 65.44f, 266.12f, 72.32f, 279.19f, 76.02f)
                    curveTo(319.84f, 87.52f, 343.94f, 129.15f, 333.61f, 169.98f)
                    curveTo(330.29f, 183.12f, 330.29f, 196.88f, 333.61f, 210.02f)
                    curveTo(343.94f, 250.85f, 319.84f, 292.48f, 279.19f, 303.98f)
                    curveTo(266.12f, 307.68f, 254.18f, 314.56f, 244.42f, 324f)
                    curveTo(214.11f, 353.33f, 165.89f, 353.33f, 135.58f, 324f)
                    curveTo(125.82f, 314.56f, 113.88f, 307.68f, 100.81f, 303.98f)
                    curveTo(60.16f, 292.48f, 36.06f, 250.85f, 46.39f, 210.02f)
                    curveTo(49.71f, 196.88f, 49.71f, 183.12f, 46.39f, 169.98f)
                    curveTo(36.06f, 129.15f, 60.16f, 87.52f, 100.81f, 76.02f)
                    curveTo(113.88f, 72.32f, 125.82f, 65.44f, 135.58f, 56f)
                    close()
                }
            }.build()

        return _IcOutline1!!
    }

@Suppress("ObjectPropertyName")
private var _IcOutline1: ImageVector? = null
