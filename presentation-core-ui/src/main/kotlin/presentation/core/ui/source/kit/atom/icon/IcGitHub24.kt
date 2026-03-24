package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IcGitHub24: ImageVector
    get() {
        if (_IcGitHub24 != null) {
            return _IcGitHub24!!
        }
        _IcGitHub24 =
            ImageVector.Builder(
                name = "IcGitHub24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                group(
                    clipPathData =
                    PathData {
                        moveTo(0f, 0f)
                        horizontalLineToRelative(24f)
                        verticalLineToRelative(24f)
                        horizontalLineToRelative(-24f)
                        close()
                    },
                ) {
                    path(
                        stroke = SolidColor(Color.Black),
                        strokeLineWidth = 2f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    ) {
                        moveTo(9f, 19f)
                        curveTo(4f, 20.5f, 4f, 16.5f, 2f, 16f)
                        moveTo(16f, 22f)
                        verticalLineTo(18.13f)
                        curveTo(16.038f, 17.653f, 15.973f, 17.174f, 15.811f, 16.724f)
                        curveTo(15.649f, 16.274f, 15.393f, 15.863f, 15.06f, 15.52f)
                        curveTo(18.2f, 15.17f, 21.5f, 13.98f, 21.5f, 8.52f)
                        curveTo(21.5f, 7.124f, 20.963f, 5.781f, 20f, 4.77f)
                        curveTo(20.456f, 3.548f, 20.424f, 2.198f, 19.91f, 1f)
                        curveTo(19.91f, 1f, 18.73f, 0.65f, 16f, 2.48f)
                        curveTo(13.708f, 1.859f, 11.292f, 1.859f, 9f, 2.48f)
                        curveTo(6.27f, 0.65f, 5.09f, 1f, 5.09f, 1f)
                        curveTo(4.576f, 2.198f, 4.544f, 3.548f, 5f, 4.77f)
                        curveTo(4.03f, 5.789f, 3.493f, 7.143f, 3.5f, 8.55f)
                        curveTo(3.5f, 13.97f, 6.8f, 15.16f, 9.94f, 15.55f)
                        curveTo(9.611f, 15.89f, 9.357f, 16.295f, 9.195f, 16.74f)
                        curveTo(9.033f, 17.184f, 8.967f, 17.658f, 9f, 18.13f)
                        verticalLineTo(22f)
                    }
                }
            }.build()

        return _IcGitHub24!!
    }

@Suppress("ObjectPropertyName")
private var _IcGitHub24: ImageVector? = null
