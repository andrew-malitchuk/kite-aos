package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcTwitter24: ImageVector
    get() {
        if (_IcTwitter24 != null) {
            return _IcTwitter24!!
        }
        _IcTwitter24 =
            ImageVector.Builder(
                name = "IcTwitter24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(23f, 3f)
                    curveTo(22.042f, 3.676f, 20.982f, 4.192f, 19.86f, 4.53f)
                    curveTo(19.258f, 3.838f, 18.457f, 3.347f, 17.567f, 3.124f)
                    curveTo(16.677f, 2.901f, 15.74f, 2.957f, 14.882f, 3.284f)
                    curveTo(14.025f, 3.612f, 13.288f, 4.194f, 12.773f, 4.954f)
                    curveTo(12.257f, 5.713f, 11.988f, 6.612f, 12f, 7.53f)
                    verticalLineTo(8.53f)
                    curveTo(10.243f, 8.576f, 8.501f, 8.186f, 6.931f, 7.395f)
                    curveTo(5.361f, 6.605f, 4.01f, 5.439f, 3f, 4f)
                    curveTo(3f, 4f, -1f, 13f, 8f, 17f)
                    curveTo(5.941f, 18.398f, 3.487f, 19.099f, 1f, 19f)
                    curveTo(10f, 24f, 21f, 19f, 21f, 7.5f)
                    curveTo(20.999f, 7.221f, 20.972f, 6.944f, 20.92f, 6.67f)
                    curveTo(21.941f, 5.664f, 22.661f, 4.393f, 23f, 3f)
                    close()
                }
            }.build()

        return _IcTwitter24!!
    }

@Suppress("ObjectPropertyName")
private var _IcTwitter24: ImageVector? = null
