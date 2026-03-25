package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcRefresh24: ImageVector
    get() {
        if (_IcRefresh24 != null) {
            return _IcRefresh24!!
        }
        _IcRefresh24 =
            ImageVector.Builder(
                name = "IcRefresh24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(12f, 20f)
                    curveTo(9.767f, 20f, 7.875f, 19.225f, 6.325f, 17.675f)
                    curveTo(4.775f, 16.125f, 4f, 14.233f, 4f, 12f)
                    curveTo(4f, 9.767f, 4.775f, 7.875f, 6.325f, 6.325f)
                    curveTo(7.875f, 4.775f, 9.767f, 4f, 12f, 4f)
                    curveTo(13.15f, 4f, 14.25f, 4.238f, 15.3f, 4.713f)
                    curveTo(16.35f, 5.188f, 17.25f, 5.867f, 18f, 6.75f)
                    verticalLineTo(4f)
                    horizontalLineTo(20f)
                    verticalLineTo(11f)
                    horizontalLineTo(13f)
                    verticalLineTo(9f)
                    horizontalLineTo(17.2f)
                    curveTo(16.667f, 8.067f, 15.938f, 7.333f, 15.012f, 6.8f)
                    curveTo(14.087f, 6.267f, 13.083f, 6f, 12f, 6f)
                    curveTo(10.333f, 6f, 8.917f, 6.583f, 7.75f, 7.75f)
                    curveTo(6.583f, 8.917f, 6f, 10.333f, 6f, 12f)
                    curveTo(6f, 13.667f, 6.583f, 15.083f, 7.75f, 16.25f)
                    curveTo(8.917f, 17.417f, 10.333f, 18f, 12f, 18f)
                    curveTo(13.283f, 18f, 14.442f, 17.633f, 15.475f, 16.9f)
                    curveTo(16.508f, 16.167f, 17.233f, 15.2f, 17.65f, 14f)
                    lineTo(19.75f, 14f)
                    curveTo(19.283f, 15.767f, 18.333f, 17.208f, 16.9f, 18.325f)
                    curveTo(15.467f, 19.442f, 13.833f, 20f, 12f, 20f)
                    close()
                }
            }.build()

        return _IcRefresh24!!
    }

@Suppress("ObjectPropertyName")
private var _IcRefresh24: ImageVector? = null
