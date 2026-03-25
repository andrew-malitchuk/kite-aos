package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcClose24: ImageVector
    get() {
        if (_IcClose24 != null) {
            return _IcClose24!!
        }
        _IcClose24 =
            ImageVector.Builder(
                name = "IcClose24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(6.4f, 19f)
                    lineTo(5f, 17.6f)
                    lineTo(10.6f, 12f)
                    lineTo(5f, 6.4f)
                    lineTo(6.4f, 5f)
                    lineTo(12f, 10.6f)
                    lineTo(17.6f, 5f)
                    lineTo(19f, 6.4f)
                    lineTo(13.4f, 12f)
                    lineTo(19f, 17.6f)
                    lineTo(17.6f, 19f)
                    lineTo(12f, 13.4f)
                    lineTo(6.4f, 19f)
                    close()
                }
            }.build()

        return _IcClose24!!
    }

@Suppress("ObjectPropertyName")
private var _IcClose24: ImageVector? = null
