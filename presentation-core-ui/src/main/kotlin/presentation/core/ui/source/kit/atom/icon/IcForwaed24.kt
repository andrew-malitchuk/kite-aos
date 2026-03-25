package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcForward24: ImageVector
    get() {
        if (_IcForward24 != null) {
            return _IcForward24!!
        }
        _IcForward24 =
            ImageVector.Builder(
                name = "IcForward24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(16.175f, 13f)
                    horizontalLineTo(4f)
                    verticalLineTo(11f)
                    horizontalLineTo(16.175f)
                    lineTo(10.575f, 5.4f)
                    lineTo(12f, 4f)
                    lineTo(20f, 12f)
                    lineTo(12f, 20f)
                    lineTo(10.575f, 18.6f)
                    lineTo(16.175f, 13f)
                    close()
                }
            }.build()

        return _IcForward24!!
    }

@Suppress("ObjectPropertyName")
private var _IcForward24: ImageVector? = null
