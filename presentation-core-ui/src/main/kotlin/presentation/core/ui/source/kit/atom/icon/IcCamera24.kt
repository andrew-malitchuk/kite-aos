package presentation.core.ui.source.kit.atom.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 24dp camera icon.
 *
 * @since 0.0.1
 */
public val IcCamera24: ImageVector
    get() {
        if (_IcCamera24 != null) {
            return _IcCamera24!!
        }
        _IcCamera24 =
            ImageVector.Builder(
                name = "IcCamera24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f,
            ).apply {
                path(fill = SolidColor(Color(0xFF1F1F1F))) {
                    moveTo(480f, 520f)
                    close()
                    moveTo(826f, 280f)
                    quadToRelative(0f, -78f, -54f, -132f)
                    reflectiveQuadToRelative(-132f, -54f)
                    verticalLineToRelative(-54f)
                    quadToRelative(100f, 0f, 170f, 70f)
                    reflectiveQuadToRelative(70f, 170f)
                    horizontalLineToRelative(-54f)
                    close()
                    moveTo(720f, 280f)
                    quadToRelative(0f, -33f, -23.5f, -56.5f)
                    reflectiveQuadTo(640f, 200f)
                    verticalLineToRelative(-54f)
                    quadToRelative(55f, 0f, 93.5f, 39f)
                    reflectiveQuadToRelative(40.5f, 95f)
                    horizontalLineToRelative(-54f)
                    close()
                    moveTo(160f, 840f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(80f, 760f)
                    verticalLineToRelative(-480f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(160f, 200f)
                    horizontalLineToRelative(126f)
                    lineToRelative(74f, -80f)
                    horizontalLineToRelative(240f)
                    verticalLineToRelative(80f)
                    lineTo(395f, 200f)
                    lineToRelative(-73f, 80f)
                    lineTo(160f, 280f)
                    verticalLineToRelative(480f)
                    horizontalLineToRelative(640f)
                    verticalLineToRelative(-440f)
                    horizontalLineToRelative(80f)
                    verticalLineToRelative(440f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(800f, 840f)
                    lineTo(160f, 840f)
                    close()
                    moveTo(480f, 700f)
                    quadToRelative(75f, 0f, 127.5f, -52.5f)
                    reflectiveQuadTo(660f, 520f)
                    quadToRelative(0f, -75f, -52.5f, -127.5f)
                    reflectiveQuadTo(480f, 340f)
                    quadToRelative(-75f, 0f, -127.5f, 52.5f)
                    reflectiveQuadTo(300f, 520f)
                    quadToRelative(0f, 75f, 52.5f, 127.5f)
                    reflectiveQuadTo(480f, 700f)
                    close()
                    moveTo(480f, 620f)
                    quadToRelative(-42f, 0f, -71f, -29f)
                    reflectiveQuadToRelative(-29f, -71f)
                    quadToRelative(0f, -42f, 29f, -71f)
                    reflectiveQuadToRelative(71f, -29f)
                    quadToRelative(42f, 0f, 71f, 29f)
                    reflectiveQuadToRelative(29f, 71f)
                    quadToRelative(0f, 42f, -29f, 71f)
                    reflectiveQuadToRelative(-71f, 29f)
                    close()
                }
            }.build()

        return _IcCamera24!!
    }

@Suppress("ObjectPropertyName")
private var _IcCamera24: ImageVector? = null
