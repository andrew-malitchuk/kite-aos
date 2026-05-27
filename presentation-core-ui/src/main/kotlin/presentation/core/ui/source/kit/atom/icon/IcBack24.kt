package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp back navigation icon.
 *
 * @since 0.0.1
 */
public val IcBack24: ImageVector
    get() {
        if (_IcBack24 != null) {
            return _IcBack24!!
        }
        _IcBack24 =
            ImageVector.Builder(
                name = "IcBack24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(7.825f, 13f)
                    lineTo(13.425f, 18.6f)
                    lineTo(12f, 20f)
                    lineTo(4f, 12f)
                    lineTo(12f, 4f)
                    lineTo(13.425f, 5.4f)
                    lineTo(7.825f, 11f)
                    horizontalLineTo(20f)
                    verticalLineTo(13f)
                    horizontalLineTo(7.825f)
                    close()
                }
            }.build()

        return _IcBack24!!
    }

@Suppress("ObjectPropertyName")
private var _IcBack24: ImageVector? = null
