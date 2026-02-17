package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcArrowLeft24: ImageVector
    get() {
        if (_IcArrowLeft24 != null) {
            return _IcArrowLeft24!!
        }
        _IcArrowLeft24 = ImageVector.Builder(
            name = "IcArrowLeft24",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF1F1F1F))) {
                moveTo(14f, 18f)
                lineTo(8f, 12f)
                lineTo(14f, 6f)
                lineTo(15.4f, 7.4f)
                lineTo(10.8f, 12f)
                lineTo(15.4f, 16.6f)
                lineTo(14f, 18f)
                close()
            }
        }.build()

        return _IcArrowLeft24!!
    }

@Suppress("ObjectPropertyName")
private var _IcArrowLeft24: ImageVector? = null
