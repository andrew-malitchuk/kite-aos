package presentation.core.ui.source.kit.atom.container

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A layout composable that detects whether the available height exceeds the available width
 * and renders different content accordingly.
 *
 * This is useful for adaptive layouts that need to switch between portrait-oriented and
 * landscape-oriented content based on the container constraints.
 *
 * @param modifier Modifier to be applied to the [BoxWithConstraints].
 * @param byHeight composable content to display when the container is taller than it is wide (portrait).
 * @param byWidth composable content to display when the container is wider than it is tall (landscape).
 *
 * @see <a href="https://www.figma.com/design/STUB_REPLACE_ME">Figma</a>
 *
 * @since 0.0.1
 */
@Composable
public fun MaxSideDetector(
    modifier: Modifier = Modifier,
    byHeight: @Composable () -> Unit,
    byWidth: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (maxWidth < maxHeight) {
            true -> byHeight()
            false -> byWidth()
        }
    }
}
