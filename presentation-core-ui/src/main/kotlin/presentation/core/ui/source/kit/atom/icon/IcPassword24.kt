import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp password / lock icon.
 *
 * @since 0.0.1
 */
public val IcPassword24: ImageVector
    get() {
        if (_IcPassword24 != null) {
            return _IcPassword24!!
        }
        _IcPassword24 =
            ImageVector.Builder(
                name = "IcPassword24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(5.588f, 13.413f)
                    curveTo(5.196f, 13.021f, 5f, 12.55f, 5f, 12f)
                    curveTo(5f, 11.45f, 5.196f, 10.979f, 5.588f, 10.587f)
                    curveTo(5.979f, 10.196f, 6.45f, 10f, 7f, 10f)
                    curveTo(7.55f, 10f, 8.021f, 10.196f, 8.413f, 10.587f)
                    curveTo(8.804f, 10.979f, 9f, 11.45f, 9f, 12f)
                    curveTo(9f, 12.55f, 8.804f, 13.021f, 8.413f, 13.413f)
                    curveTo(8.021f, 13.804f, 7.55f, 14f, 7f, 14f)
                    curveTo(6.45f, 14f, 5.979f, 13.804f, 5.588f, 13.413f)
                    close()
                    moveTo(7f, 18f)
                    curveTo(5.333f, 18f, 3.917f, 17.417f, 2.75f, 16.25f)
                    curveTo(1.583f, 15.083f, 1f, 13.667f, 1f, 12f)
                    curveTo(1f, 10.333f, 1.583f, 8.917f, 2.75f, 7.75f)
                    curveTo(3.917f, 6.583f, 5.333f, 6f, 7f, 6f)
                    curveTo(8.117f, 6f, 9.129f, 6.275f, 10.038f, 6.825f)
                    curveTo(10.946f, 7.375f, 11.667f, 8.1f, 12.2f, 9f)
                    horizontalLineTo(21f)
                    lineTo(24f, 12f)
                    lineTo(19.5f, 16.5f)
                    lineTo(17.5f, 15f)
                    lineTo(15.5f, 16.5f)
                    lineTo(13.375f, 15f)
                    horizontalLineTo(12.2f)
                    curveTo(11.667f, 15.9f, 10.946f, 16.625f, 10.038f, 17.175f)
                    curveTo(9.129f, 17.725f, 8.117f, 18f, 7f, 18f)
                    close()
                    moveTo(7f, 16f)
                    curveTo(7.933f, 16f, 8.754f, 15.717f, 9.462f, 15.15f)
                    curveTo(10.171f, 14.583f, 10.642f, 13.867f, 10.875f, 13f)
                    horizontalLineTo(14f)
                    lineTo(15.45f, 14.025f)
                    lineTo(17.5f, 12.5f)
                    lineTo(19.275f, 13.875f)
                    lineTo(21.15f, 12f)
                    lineTo(20.15f, 11f)
                    horizontalLineTo(10.875f)
                    curveTo(10.642f, 10.133f, 10.171f, 9.417f, 9.462f, 8.85f)
                    curveTo(8.754f, 8.283f, 7.933f, 8f, 7f, 8f)
                    curveTo(5.9f, 8f, 4.958f, 8.392f, 4.175f, 9.175f)
                    curveTo(3.392f, 9.958f, 3f, 10.9f, 3f, 12f)
                    curveTo(3f, 13.1f, 3.392f, 14.042f, 4.175f, 14.825f)
                    curveTo(4.958f, 15.608f, 5.9f, 16f, 7f, 16f)
                    close()
                }
            }.build()

        return _IcPassword24!!
    }

@Suppress("ObjectPropertyName")
private var _IcPassword24: ImageVector? = null
