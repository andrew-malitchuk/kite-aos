import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp client ID / identification card icon.
 *
 * @since 0.0.1
 */
public val IcClientId: ImageVector
    get() {
        if (_IcClientId != null) {
            return _IcClientId!!
        }
        _IcClientId =
            ImageVector.Builder(
                name = "IcClientId",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(14f, 13f)
                    horizontalLineTo(19f)
                    verticalLineTo(11f)
                    horizontalLineTo(14f)
                    verticalLineTo(13f)
                    close()
                    moveTo(14f, 10f)
                    horizontalLineTo(19f)
                    verticalLineTo(8f)
                    horizontalLineTo(14f)
                    verticalLineTo(10f)
                    close()
                    moveTo(5f, 16f)
                    horizontalLineTo(13f)
                    verticalLineTo(15.45f)
                    curveTo(13f, 14.7f, 12.633f, 14.104f, 11.9f, 13.663f)
                    curveTo(11.167f, 13.221f, 10.2f, 13f, 9f, 13f)
                    curveTo(7.8f, 13f, 6.833f, 13.221f, 6.1f, 13.663f)
                    curveTo(5.367f, 14.104f, 5f, 14.7f, 5f, 15.45f)
                    verticalLineTo(16f)
                    close()
                    moveTo(10.413f, 11.413f)
                    curveTo(10.804f, 11.021f, 11f, 10.55f, 11f, 10f)
                    curveTo(11f, 9.45f, 10.804f, 8.979f, 10.413f, 8.587f)
                    curveTo(10.021f, 8.196f, 9.55f, 8f, 9f, 8f)
                    curveTo(8.45f, 8f, 7.979f, 8.196f, 7.588f, 8.587f)
                    curveTo(7.196f, 8.979f, 7f, 9.45f, 7f, 10f)
                    curveTo(7f, 10.55f, 7.196f, 11.021f, 7.588f, 11.413f)
                    curveTo(7.979f, 11.804f, 8.45f, 12f, 9f, 12f)
                    curveTo(9.55f, 12f, 10.021f, 11.804f, 10.413f, 11.413f)
                    close()
                    moveTo(4f, 20f)
                    curveTo(3.45f, 20f, 2.979f, 19.804f, 2.588f, 19.413f)
                    curveTo(2.196f, 19.021f, 2f, 18.55f, 2f, 18f)
                    verticalLineTo(6f)
                    curveTo(2f, 5.45f, 2.196f, 4.979f, 2.588f, 4.588f)
                    curveTo(2.979f, 4.196f, 3.45f, 4f, 4f, 4f)
                    horizontalLineTo(20f)
                    curveTo(20.55f, 4f, 21.021f, 4.196f, 21.413f, 4.588f)
                    curveTo(21.804f, 4.979f, 22f, 5.45f, 22f, 6f)
                    verticalLineTo(18f)
                    curveTo(22f, 18.55f, 21.804f, 19.021f, 21.413f, 19.413f)
                    curveTo(21.021f, 19.804f, 20.55f, 20f, 20f, 20f)
                    horizontalLineTo(4f)
                    close()
                    moveTo(4f, 18f)
                    horizontalLineTo(20f)
                    verticalLineTo(6f)
                    horizontalLineTo(4f)
                    verticalLineTo(18f)
                    close()
                }
            }.build()

        return _IcClientId!!
    }

@Suppress("ObjectPropertyName")
private var _IcClientId: ImageVector? = null
