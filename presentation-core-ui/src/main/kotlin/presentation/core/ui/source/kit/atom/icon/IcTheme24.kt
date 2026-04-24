package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp theme / appearance icon.
 *
 * @since 0.0.1
 */
public val IcTheme24: ImageVector
    get() {
        if (_IcTheme24 != null) {
            return _IcTheme24!!
        }
        _IcTheme24 =
            ImageVector.Builder(
                name = "IcTheme24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(8.65f, 20.5f)
                    lineTo(2.5f, 14.35f)
                    curveTo(2.333f, 14.183f, 2.208f, 14f, 2.125f, 13.8f)
                    curveTo(2.042f, 13.6f, 2f, 13.392f, 2f, 13.175f)
                    curveTo(2f, 12.958f, 2.042f, 12.75f, 2.125f, 12.55f)
                    curveTo(2.208f, 12.35f, 2.333f, 12.167f, 2.5f, 12f)
                    lineTo(8.25f, 6.275f)
                    lineTo(5.6f, 3.625f)
                    lineTo(7.15f, 2f)
                    lineTo(17.15f, 12f)
                    curveTo(17.317f, 12.167f, 17.438f, 12.35f, 17.513f, 12.55f)
                    curveTo(17.587f, 12.75f, 17.625f, 12.958f, 17.625f, 13.175f)
                    curveTo(17.625f, 13.392f, 17.587f, 13.6f, 17.513f, 13.8f)
                    curveTo(17.438f, 14f, 17.317f, 14.183f, 17.15f, 14.35f)
                    lineTo(11f, 20.5f)
                    curveTo(10.833f, 20.667f, 10.65f, 20.792f, 10.45f, 20.875f)
                    curveTo(10.25f, 20.958f, 10.042f, 21f, 9.825f, 21f)
                    curveTo(9.608f, 21f, 9.4f, 20.958f, 9.2f, 20.875f)
                    curveTo(9f, 20.792f, 8.817f, 20.667f, 8.65f, 20.5f)
                    close()
                    moveTo(9.825f, 7.85f)
                    lineTo(4.475f, 13.2f)
                    horizontalLineTo(15.175f)
                    lineTo(9.825f, 7.85f)
                    close()
                    moveTo(19.8f, 21f)
                    curveTo(19.2f, 21f, 18.692f, 20.788f, 18.275f, 20.362f)
                    curveTo(17.858f, 19.938f, 17.65f, 19.417f, 17.65f, 18.8f)
                    curveTo(17.65f, 18.35f, 17.763f, 17.925f, 17.987f, 17.525f)
                    curveTo(18.212f, 17.125f, 18.467f, 16.733f, 18.75f, 16.35f)
                    lineTo(19.8f, 15f)
                    lineTo(20.9f, 16.35f)
                    curveTo(21.167f, 16.733f, 21.417f, 17.125f, 21.65f, 17.525f)
                    curveTo(21.883f, 17.925f, 22f, 18.35f, 22f, 18.8f)
                    curveTo(22f, 19.417f, 21.783f, 19.938f, 21.35f, 20.362f)
                    curveTo(20.917f, 20.788f, 20.4f, 21f, 19.8f, 21f)
                    close()
                }
            }.build()

        return _IcTheme24!!
    }

@Suppress("ObjectPropertyName")
private var _IcTheme24: ImageVector? = null
