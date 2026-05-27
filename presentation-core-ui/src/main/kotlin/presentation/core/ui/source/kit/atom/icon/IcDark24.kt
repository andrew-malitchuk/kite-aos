package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp dark mode (moon) icon.
 *
 * @since 0.0.1
 */
public val IcDark24: ImageVector
    get() {
        if (_IcDark24 != null) {
            return _IcDark24!!
        }
        _IcDark24 =
            ImageVector.Builder(
                name = "IcDark24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(12f, 21f)
                    curveTo(9.5f, 21f, 7.375f, 20.125f, 5.625f, 18.375f)
                    curveTo(3.875f, 16.625f, 3f, 14.5f, 3f, 12f)
                    curveTo(3f, 9.5f, 3.875f, 7.375f, 5.625f, 5.625f)
                    curveTo(7.375f, 3.875f, 9.5f, 3f, 12f, 3f)
                    curveTo(12.233f, 3f, 12.462f, 3.008f, 12.688f, 3.025f)
                    curveTo(12.913f, 3.042f, 13.133f, 3.067f, 13.35f, 3.1f)
                    curveTo(12.667f, 3.583f, 12.121f, 4.213f, 11.712f, 4.988f)
                    curveTo(11.304f, 5.762f, 11.1f, 6.6f, 11.1f, 7.5f)
                    curveTo(11.1f, 9f, 11.625f, 10.275f, 12.675f, 11.325f)
                    curveTo(13.725f, 12.375f, 15f, 12.9f, 16.5f, 12.9f)
                    curveTo(17.417f, 12.9f, 18.258f, 12.696f, 19.025f, 12.288f)
                    curveTo(19.792f, 11.879f, 20.417f, 11.333f, 20.9f, 10.65f)
                    curveTo(20.933f, 10.867f, 20.958f, 11.087f, 20.975f, 11.313f)
                    curveTo(20.992f, 11.538f, 21f, 11.767f, 21f, 12f)
                    curveTo(21f, 14.5f, 20.125f, 16.625f, 18.375f, 18.375f)
                    curveTo(16.625f, 20.125f, 14.5f, 21f, 12f, 21f)
                    close()
                    moveTo(12f, 19f)
                    curveTo(13.467f, 19f, 14.783f, 18.596f, 15.95f, 17.788f)
                    curveTo(17.117f, 16.979f, 17.967f, 15.925f, 18.5f, 14.625f)
                    curveTo(18.167f, 14.708f, 17.833f, 14.775f, 17.5f, 14.825f)
                    curveTo(17.167f, 14.875f, 16.833f, 14.9f, 16.5f, 14.9f)
                    curveTo(14.45f, 14.9f, 12.704f, 14.179f, 11.262f, 12.738f)
                    curveTo(9.821f, 11.296f, 9.1f, 9.55f, 9.1f, 7.5f)
                    curveTo(9.1f, 7.167f, 9.125f, 6.833f, 9.175f, 6.5f)
                    curveTo(9.225f, 6.167f, 9.292f, 5.833f, 9.375f, 5.5f)
                    curveTo(8.075f, 6.033f, 7.021f, 6.883f, 6.213f, 8.05f)
                    curveTo(5.404f, 9.217f, 5f, 10.533f, 5f, 12f)
                    curveTo(5f, 13.933f, 5.683f, 15.583f, 7.05f, 16.95f)
                    curveTo(8.417f, 18.317f, 10.067f, 19f, 12f, 19f)
                    close()
                }
            }.build()

        return _IcDark24!!
    }

@Suppress("ObjectPropertyName")
private var _IcDark24: ImageVector? = null
