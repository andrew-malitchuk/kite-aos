package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcArrowUp24: ImageVector
    get() {
        if (_IcArrowUp24 != null) {
            return _IcArrowUp24!!
        }
        _IcArrowUp24 = ImageVector.Builder(
            name = "IcArrowUp24",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF1F1F1F))) {
                moveTo(12f, 10.8f)
                lineTo(7.4f, 15.4f)
                lineTo(6f, 14f)
                lineTo(12f, 8f)
                lineTo(18f, 14f)
                lineTo(16.6f, 15.4f)
                lineTo(12f, 10.8f)
                close()
            }
        }.build()

        return _IcArrowUp24!!
    }

@Suppress("ObjectPropertyName")
private var _IcArrowUp24: ImageVector? = null
